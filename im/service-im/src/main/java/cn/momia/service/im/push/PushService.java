package cn.momia.service.im.push;

import java.util.Collection;

public interface PushService {
    boolean push(long userId, PushMsg msg);
    boolean push(Collection<Long> userIds, PushMsg msg);
}
