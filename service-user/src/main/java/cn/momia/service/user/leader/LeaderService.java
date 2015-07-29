package cn.momia.service.user.leader;

import cn.momia.common.service.Service;

public interface LeaderService extends Service {
    String getDesc();

    Leader getByUser(long userId);
    long add(Leader leader);
    boolean update(Leader leader);
    boolean deleteByUser(long userId);
    boolean reapply(Leader leader);
}
