package cn.momia.service.im.impl;

import cn.momia.api.im.dto.Group;
import cn.momia.api.im.dto.Member;
import cn.momia.common.service.AbstractService;
import cn.momia.service.im.ImService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractImService extends AbstractService implements ImService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractImService.class);

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
            String sql = "INSERT INTO SG_ImGroup (GroupId, GroupName, CourseId, CourseSkuId, AddTime) VALUES (?, ?, ?, ?, NOW())";
            update(sql, new Object[] { groupId, groupName, courseId, courseSkuId });
        } catch (Exception e) {
            LOGGER.error("fail to log group info for group: {}", groupId, e);
        }
    }

    private void logGroupMembers(long groupId, Collection<Long> userIds, boolean teacher) {
        String sql = "INSERT INTO SG_ImGroupMember (GroupId, UserId, Teacher, AddTime) VALUES (?, ?, ?, NOW())";
        List<Object[]> args = new ArrayList<Object[]>();
        for (long userId : userIds) {
            args.add(new Object[] { groupId, userId, teacher });
        }
        batchUpdate(sql, args);
    }

    @Override
    public boolean updateGroupName(long courseId, long courseSkuId, String groupName) {
        long groupId = courseSkuId;
        if (doUpdateGroupName(groupId, groupName)) {
            return updateGroupNameLog(groupId, groupName, courseId, courseSkuId);
        }

        return false;
    }

    protected abstract boolean doUpdateGroupName(long groupId, String groupName);

    private boolean updateGroupNameLog(long groupId, String groupName, long courseId, long courseSkuId) {
        String sql = "UPDATE SG_ImGroup SET GroupName=? WHERE GroupId=? AND CourseId=? AND CourseSkuId=? AND Status<>0";
        return update(sql, new Object[] { groupName, groupId, courseId, courseSkuId });
    }

    @Override
    public Group getGroup(long groupId) {
        Set<Long> groupIds = Sets.newHashSet(groupId);
        List<Group> groups = listGroups(groupIds);

        return groups.isEmpty() ? Group.NOT_EXIST_GROUP : groups.get(0);
    }

    @Override
    public List<Group> listGroups(Collection<Long> groupIds) {
        if (groupIds.isEmpty()) return new ArrayList<Group>();

        String sql = "SELECT GroupId, GroupName, CourseId, CourseSkuId FROM SG_ImGroup WHERE GroupId IN (" + StringUtils.join(groupIds, ",") + ") AND Status<>0";
        return queryObjectList(sql, Group.class);
    }

    @Override
    public boolean isInGroup(long userId, long groupId) {
        String sql = "SELECT COUNT(1) FROM SG_ImGroupMember WHERE UserId=? AND GroupId=? AND Status<>0";
        return queryInt(sql, new Object[] { userId, groupId }) > 0;
    }

    @Override
    public List<Member> queryMembersByGroup(long groupId) {
        String sql = "SELECT Id FROM SG_ImGroupMember WHERE GroupId=? AND Status<>0 ORDER BY Teacher DESC, AddTime ASC";
        List<Long> memberIds = queryLongList(sql, new Object[] { groupId });

        return listMembers(memberIds);
    }

    private List<Member> listMembers(Collection<Long> memberIds) {
        if (memberIds.isEmpty()) return new ArrayList<Member>();

        String sql = "SELECT Id, GroupId, UserId, Teacher, AddTime FROM SG_ImGroupMember WHERE Id IN (" + StringUtils.join(memberIds, ",") + ")";
        List<Member> members = queryObjectList(sql, Member.class);

        Map<Long, Member> membersMap = new HashMap<Long, Member>();
        for (Member member : members) {
            membersMap.put(member.getId(), member);
        }

        List<Member> result = new ArrayList<Member>();
        for (long memberId : memberIds) {
            Member member = membersMap.get(memberId);
            if (member != null) result.add(member);
        }

        return result;
    }

    @Override
    public List<Member> queryMembersByUser(long userId) {
        String sql = "SELECT Id FROM SG_ImGroupMember WHERE UserId=? AND Status<>0 ORDER BY AddTime ASC";
        List<Long> memberIds = queryLongList(sql, new Object[] { userId });

        return listMembers(memberIds);
    }

    @Override
    public boolean joinGroup(long courseId, long courseSkuId, long userId, boolean teacher) {
        Group group = queryGroup(courseId, courseSkuId);
        if (!group.exists()) return false;

        if (doJoinGroup(group.getGroupId(), group.getGroupName(), userId)) {
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

    protected abstract boolean doJoinGroup(long groupId, String groupName, long userId);

    @Override
    public boolean leaveGroup(long courseId, long courseSkuId, long userId) {
        Group group = queryGroup(courseId, courseSkuId);
        if (!group.exists()) return false;

        if (doLeaveGroup(group.getGroupId(), userId)) {
            deleteGroupMembersLog(group.getGroupId(), Sets.newHashSet(userId));
            return true;
        }

        return false;
    }

    protected abstract boolean doLeaveGroup(long groupId, long userId);

    private void deleteGroupMembersLog(long groupId, Collection<Long> userIds) {
        String sql = "UPDATE SG_ImGroupMember SET Status=0 WHERE GroupId=? AND UserId=?";
        List<Object[]> args = new ArrayList<Object[]>();
        for (long userId : userIds) {
            args.add(new Object[] { groupId, userId });
        }
        batchUpdate(sql, args);
    }
}
