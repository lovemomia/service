package cn.momia.service.im;

import cn.momia.api.im.dto.Member;

import java.util.Collection;
import java.util.List;

public interface ImService {
    boolean createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName);
    boolean updateGroupName(long courseId, long courseSkuId, String groupName);

    boolean isInGroup(long userId, long groupId);
    List<Member> listMembers(long groupId);

    boolean joinGroup(long courseId, long courseSkuId, long userId, boolean teacher);
    boolean leaveGroup(long courseId, long courseSkuId, long userId);

    String generateImToken(long userId, String nickName, String avatar);
    void updateNickName(long userId, String nickName);
    void updateAvatar(long userId, String avatar);
}
