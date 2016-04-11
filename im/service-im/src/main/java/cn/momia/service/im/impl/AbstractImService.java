package cn.momia.service.im.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.im.Group;
import cn.momia.service.im.GroupMember;
import cn.momia.service.im.ImService;
import cn.momia.service.im.UserGroup;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractImService extends AbstractService implements ImService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractImService.class);

    @Override
    public List<UserGroup> listUserGroups(long userId) {
        String sql = "SELECT A.GroupId FROM SG_ImGroupMember A INNER JOIN SG_ImGroup B ON A.GroupId=B.GroupId WHERE A.UserId=? AND A.Status=1 AND B.Status=1 GROUP BY A.GroupId ORDER BY B.GroupName ASC";
        List<Long> groupIds = queryLongList(sql, new Object[] { userId });
        List<Group> groups = listGroups(groupIds);
        Map<Long, Date> joinTimes = queryJoinTimes(userId, groupIds);

        List<UserGroup> userGroups = new ArrayList<UserGroup>();
        for (Group group : groups) {
            Date joinTime = joinTimes.get(group.getGroupId());
            if (joinTime == null) continue;

            UserGroup userGroup = new UserGroup();
            userGroup.setUserId(userId);
            userGroup.setGroupId(group.getGroupId());
            userGroup.setGroupName(group.getGroupName());
            userGroup.setCourseId(group.getCourseId());
            userGroup.setCourseSkuId(group.getCourseSkuId());
            userGroup.setAddTime(joinTime);

            userGroups.add(userGroup);
        }

        return userGroups;
    }

    private List<Group> listGroups(Collection<Long> groupIds) {
        if (groupIds.isEmpty()) return new ArrayList<Group>();

        String sql = String .format("SELECT GroupId, GroupName, CourseId, CourseSkuId FROM SG_ImGroup WHERE GroupId IN (%s) AND Status=1", StringUtils.join(groupIds, ","));
        return queryObjectList(sql, Group.class);
    }

    public Map<Long, Date> queryJoinTimes(long userId, Collection<Long> groupIds) {
        if (groupIds.isEmpty()) return new HashMap<Long, Date>();

        String sql = String.format("SELECT GroupId, AddTime FROM SG_ImGroupMember WHERE UserId=? AND GroupId IN (%s)", StringUtils.join(groupIds, ","));
        return queryMap(sql, new Object[] { userId }, Long.class, Date.class);
    }

    @Override
    public Group getGroup(long groupId) {
        List<Group> groups = listGroups(Sets.newHashSet(groupId));
        return groups.isEmpty() ? Group.NOT_EXIST_GROUP : groups.get(0);
    }

    @Override
    public boolean isInGroup(long userId, long groupId) {
        String sql = "SELECT COUNT(1) FROM SG_ImGroupMember WHERE UserId=? AND GroupId=? AND Status=1";
        return queryInt(sql, new Object[] { userId, groupId }) > 0;
    }

    @Override
    public List<GroupMember> listGroupMembers(long groupId) {
        String sql = "SELECT Id, GroupId, UserId, Teacher, AddTime FROM SG_ImGroupMember WHERE GroupId=? AND Status=1 GROUP BY UserId ORDER BY MAX(Teacher) DESC, MAX(AddTime) ASC";
        return queryObjectList(sql, new Object[] { groupId }, GroupMember.class);
    }

    @Override
    public boolean joinGroup(long userId, long courseId, long courseSkuId, boolean teacher) {
        Group group = queryGroup(courseId, courseSkuId);
        if (!group.exists()) return false;

        if (doJoinGroup(userId, group.getGroupId(), group.getGroupName())) {
            logGroupMembers(group.getGroupId(), Sets.newHashSet(userId), teacher);
            return true;
        }

        return false;
    }

    private Group queryGroup(long courseId, long courseSkuId) {
        String sql = "SELECT GroupId FROM SG_ImGroup WHERE CourseId=? AND CourseSkuId=? AND Status=1 LIMIT 1";
        List<Long> groupIds = queryLongList(sql, new Object[] { courseId, courseSkuId });
        List<Group> groups = listGroups(groupIds);

        return groups.isEmpty() ? Group.NOT_EXIST_GROUP : groups.get(0);
    }

    protected abstract boolean doJoinGroup(long userId, long groupId, String groupName);

    private void logGroupMembers(long groupId, Collection<Long> userIds, boolean teacher) {
        Set<Long> existUserIds = Sets.newHashSet(getExistUserIds(groupId, userIds));
        if (!existUserIds.isEmpty()) {
            String sql = "UPDATE SG_ImGroupMember SET Status=1 WHERE GroupId=? AND UserId=?";
            List<Object[]> args = new ArrayList<Object[]>();
            for (long userId : userIds) {
                args.add(new Object[] { groupId, userId });
            }
            batchUpdate(sql, args);
        }

        String sql = "INSERT INTO SG_ImGroupMember (GroupId, UserId, Teacher, AddTime) VALUES (?, ?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (long userId : userIds) {
            if (!existUserIds.contains(userId)) args.add(new Object[] { groupId, userId, teacher });
        }
        batchUpdate(sql, args);
    }

    private List<Long> getExistUserIds(long groupId, Collection<Long> userIds) {
        if (userIds.isEmpty()) return new ArrayList<Long>();

        String sql = String.format("SELECT UserId FROM SG_ImGroupMember WHERE GroupId=? AND UserId IN (%s)", StringUtils.join(userIds, ","));
        return queryLongList(sql, new Object[] { groupId });
    }

    @Override
    public boolean leaveGroup(long userId, long courseId, long courseSkuId) {
        Group group = queryGroup(courseId, courseSkuId);
        if (!group.exists()) return false;

        if (doLeaveGroup(userId, group.getGroupId())) {
            deleteGroupMembersLog(group.getGroupId(), Sets.newHashSet(userId));
            return true;
        }

        return false;
    }

    protected abstract boolean doLeaveGroup(long userId, long groupId);

    private void deleteGroupMembersLog(long groupId, Collection<Long> userIds) {
        String sql = "UPDATE SG_ImGroupMember SET Status=0 WHERE GroupId=? AND UserId=?";
        List<Object[]> args = new ArrayList<Object[]>();
        for (long userId : userIds) {
            args.add(new Object[] { groupId, userId });
        }
        batchUpdate(sql, args);
    }

    @Override
    public boolean createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName) {
        long groupId = courseSkuId;
        if (doCreateGroup(groupId, groupName, teacherUserIds)) {
            logGroup(groupId, groupName, courseId, courseSkuId);
            logGroupMembers(groupId, teacherUserIds, true);

            return true;
        }

        return false;
    }

    protected abstract boolean doCreateGroup(long groupId, String groupName, Collection<Long> userIds);

    private void logGroup(long groupId, String groupName, long courseId, long courseSkuId) {
        try {
            if (exists(groupId)) {
                String sql = "UPDATE SG_ImGroup SET GroupName=?, CourseId=?, CourseSkuId=?, Status=1 WHERE GroupId=?";
                update(sql, new Object[] { groupName, courseId, courseSkuId, groupId });
            } else {
                String sql = "INSERT INTO SG_ImGroup (GroupId, GroupName, CourseId, CourseSkuId, AddTime) VALUES (?, ?, ?, ?, NOW())";
                update(sql, new Object[] { groupId, groupName, courseId, courseSkuId });
            }
        } catch (Exception e) {
            LOGGER.error("fail to log group info for group: {}", groupId, e);
        }
    }

    private boolean exists(long groupId) {
        String sql = "SELECT COUNT(1) FROM SG_ImGroup WHERE GroupId=?";
        return queryInt(sql, new Object[] { groupId }) > 0;
    }

    @Override
    public boolean updateGroup(long courseId, long courseSkuId, String groupName) {
        long groupId = courseSkuId;
        if (doUpdateGroupName(groupId, groupName)) {
            return updateGroupNameLog(groupId, groupName, courseId, courseSkuId);
        }

        return false;
    }

    protected abstract boolean doUpdateGroupName(long groupId, String groupName);

    private boolean updateGroupNameLog(long groupId, String groupName, long courseId, long courseSkuId) {
        String sql = "UPDATE SG_ImGroup SET GroupName=? WHERE GroupId=? AND CourseId=? AND CourseSkuId=? AND Status＝1";
        return update(sql, new Object[] { groupName, groupId, courseId, courseSkuId });
    }

    @Override
    public boolean dismissGroup(long groupId) {
        if (doDismissGroup(groupId)) {
            deleteGroupLog(groupId);
            deleteGroupMembersLog(groupId);

            return true;
        }

        return false;
    }

    protected abstract boolean doDismissGroup(long groupId);

    private void deleteGroupLog(long groupId) {
        String sql = "UPDATE SG_ImGroup SET Status=0 WHERE GroupId=?";
        update(sql, new Object[] { groupId });
    }

    private void deleteGroupMembersLog(long groupId) {
        String sql = "UPDATE SG_ImGroupMember SET Status=0 WHERE GroupId=?";
        update(sql, new Object[] { groupId });
    }
}
