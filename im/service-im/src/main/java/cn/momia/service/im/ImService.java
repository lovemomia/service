package cn.momia.service.im;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ImService {
    String generateImToken(long userId, String nickName, String avatar);
    boolean updateNickName(long userId, String nickName);
    boolean updateAvatar(long userId, String avatar);

    boolean createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName);
    boolean updateGroupName(long courseId, long courseSkuId, String groupName);
    boolean dismissGroup(long groupId);

    Group getGroup(long groupId);
    List<Group> listGroups(Collection<Long> groupIds);

    boolean isInGroup(long userId, long groupId);
    List<GroupMember> listGroupMembers(long groupId);

    boolean joinGroup(long userId, long courseId, long courseSkuId, boolean teacher);
    boolean leaveGroup(long userId, long courseId, long courseSkuId);

    List<Group> listUserGroups(long userId);
    Map<Long, Date> queryJoinTimes(long userId, Collection<Long> groupIds);
}
