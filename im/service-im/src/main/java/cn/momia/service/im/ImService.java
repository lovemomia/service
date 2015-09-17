package cn.momia.service.im;

import java.util.Collection;
import java.util.Set;

public interface ImService {
    String getToken(long userId);
    String register(long userId, String nickName, String avatar);

    Group queryGroup(long productId, long skuId);
    long createGroup(String groupName, long productId, long skuId);
    boolean initGroup(long userId, long groupId, String groupName);
    boolean joinGroup(long groupId, Collection<Long> userIds);
    boolean deleteGroup(long userId, long groupId);

    void logGroupUsers(long groupId, Set<Long> userIds);
    void deleteGroupInfo(long groupId);
}
