package cn.momia.service.user.leader;

import cn.momia.service.base.Service;

import java.util.Collection;
import java.util.List;

public interface LeaderService extends Service {
    String getDesc();

    Leader getByUser(long userId);
    List<Leader> getByUsers(Collection<Long> userIds);
    long add(Leader leader);
    boolean update(Leader leader);
    boolean deleteByUser(long userId);
    boolean reapply(Leader leader);
}
