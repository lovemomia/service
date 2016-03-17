package cn.momia.service.im;

import java.util.Collection;
import java.util.List;

public interface ImService {
    String generateImToken(long userId, String nickName, String avatar);
    boolean updateNickName(long userId, String nickName);
    boolean updateAvatar(long userId, String avatar);
    List<UserGroup> listUserGroups(long userId);

    boolean createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName);
    boolean updateGroup(long courseId, long courseSkuId, String groupName);
    boolean dismissGroup(long groupId);

    Group getGroup(long groupId);

    boolean isInGroup(long userId, long groupId);
    List<GroupMember> listGroupMembers(long groupId);

    boolean joinGroup(long userId, long courseId, long courseSkuId, boolean teacher);
    boolean leaveGroup(long userId, long courseId, long courseSkuId);
}
