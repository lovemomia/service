package cn.momia.service.product.base;

import cn.momia.service.base.Service;

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

    boolean join(long id, int count);
    boolean sold(long id, int count);
    boolean soldOut(long id);
    void unSoldOut(long id);
    void decreaseJoined(long id, int count);
}
