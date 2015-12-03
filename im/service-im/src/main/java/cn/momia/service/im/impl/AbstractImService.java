package cn.momia.service.im.impl;

import cn.momia.api.im.dto.Group;
import cn.momia.api.im.dto.Member;
import cn.momia.common.service.AbstractService;
import cn.momia.service.im.ImService;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractImService extends AbstractService implements ImService {
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
        String sql = "INSERT INTO SG_ImGroup (GroupId, GroupName, CourseId, CourseSkuId, AddTime) VALUES (?, ?, ?, ?, NOW())";
        update(sql, new Object[] { groupId, groupName, courseId, courseSkuId });
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
    public boolean isInGroup(long userId, long groupId) {
        String sql = "SELECT COUNT(1) FROM SG_ImGroupMember WHERE UserId=? AND GroupId=? AND Status<>0";
        return queryInt(sql, new Object[] { userId, groupId }) > 0;
    }

    @Override
    public List<Member> listMembers(long groupId) {
        String sql = "SELECT Id, GroupId, UserId, Teacher FROM SG_ImGroupMember WHERE GroupId=? AND Status<>0 ORDER BY Teacher DESC, AddTime ASC";
        return queryObjectList(sql, new Object[] { groupId }, Member.class);
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
        String sql = "SELECT GroupId, GroupName, CourseId, CourseSkuId FROM SG_ImGroup WHERE CourseId=? AND CourseSkuId=? AND Status=1 LIMIT 1";
        return queryObject(sql, new Object[] { courseId, courseSkuId }, Group.class, Group.NOT_EXIST_GROUP);
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
