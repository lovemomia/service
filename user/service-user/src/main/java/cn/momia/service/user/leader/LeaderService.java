package cn.momia.service.user.leader;

import java.util.Collection;
import java.util.List;

public interface LeaderService {
    long add(Leader leader);

    Leader getByUser(long userId);
    List<Leader> listByUsers(Collection<Long> userIds);

    boolean update(Leader leader);
    boolean deleteByUser(long userId);
}
