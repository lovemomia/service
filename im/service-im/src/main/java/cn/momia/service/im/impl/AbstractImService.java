package cn.momia.service.im.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.im.ImService;

import java.util.Collection;

public class AbstractImService extends AbstractService implements ImService {
    @Override
    public long createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName) {
        return 0;
    }

    @Override
    public boolean updateGroupName(long courseId, long courseSkuId, String groupName) {
        return false;
    }
}
