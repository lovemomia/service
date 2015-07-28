package cn.momia.service.product.base;

import cn.momia.common.service.Service;

import java.util.Collection;
import java.util.List;

public interface BaseProductService extends Service {
    BaseProduct get(long id);
    List<BaseProduct> get(Collection<Long> ids);

    long queryCount(int cityId);
    List<BaseProduct> query(int cityId, int start, int count);
    long queryCountByWeekend(int cityId);
    List<BaseProduct> queryByWeekend(int cityId, int start, int count);
    long queryCountByMonth(int cityId, String currentMonth, String nextMonth);
    List<BaseProduct> queryByMonth(int cityId, String currentMonth, String nextMonth);

    void join(long id, int count);
    void decreaseJoined(long id, int count);
    void soldOut(long id);
    void unSoldOut(long id);
    boolean sold(long id, int count);
}
