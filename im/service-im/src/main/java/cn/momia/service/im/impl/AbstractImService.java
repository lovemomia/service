package cn.momia.service.im.impl;

import cn.momia.api.im.dto.Member;
import cn.momia.common.service.AbstractService;
import cn.momia.service.im.ImService;

import java.util.Collection;
import java.util.List;

public class AbstractImService extends AbstractService implements ImService {
    @Override
    public long createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName) {
        return 0;
    }

    @Override
    public boolean updateGroupName(long courseId, long courseSkuId, String groupName) {
        return false;
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
}
