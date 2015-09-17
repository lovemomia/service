package cn.momia.service.im;

import java.util.List;

public interface ImService {
    String getToken(long userId);
    String register(long userId, String nickName, String avatar);

    Group queryGroup(long productId, long skuId);
    long createGroup(String groupName, long productId, long skuId);
    boolean initGroup(long userId, long groupId, String groupName);
    boolean joinGroup(long groupId, List<Long> userIds);
}
