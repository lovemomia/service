package cn.momia.service.im.push.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.im.push.PushMsg;
import cn.momia.service.im.push.PushService;
import com.google.common.collect.Sets;

import java.util.Collection;

public abstract class AbstractPushService extends AbstractService implements PushService {
    protected static final long SYSTEM_PUSH_USERID = 10000;

    @Override
    public boolean push(long userId, PushMsg msg) {
        return doPush(Sets.newHashSet(userId), msg);
    }

    protected abstract boolean doPush(Collection<Long> userIds, PushMsg msg);

    @Override
    public boolean push(Collection<Long> userIds, PushMsg msg) {
        return doPush(userIds, msg);
    }
}
