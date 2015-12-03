package cn.momia.service.im;

import java.util.Collection;

public interface ImService {
    long createGroup(long courseId, long courseSkuId, Collection<Long> teacherUserIds, String groupName);
    boolean updateGroupName(long courseId, long courseSkuId, String groupName);
}
