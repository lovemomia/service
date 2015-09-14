package cn.momia.service.product.base;

import java.util.Collection;
import java.util.List;

public interface BaseProductService {
    BaseProduct get(long id);
    List<BaseProduct> get(Collection<Long> ids);
    String getDetail(long id);

    long queryCount(int cityId);
    List<BaseProduct> query(int cityId, int start, int count, ProductSort productSort);
    long queryCountByWeekend(int cityId);
    List<BaseProduct> queryByWeekend(int cityId, int start, int count);
    long queryCountByMonth(int cityId, String currentMonth, String nextMonth);
    List<BaseProduct> queryByMonth(int cityId, String currentMonth, String nextMonth);
    long queryCountNeedLeader(int cityId);
    List<BaseProduct> queryNeedLeader(int cityId, int start, int count);

    void join(long id, int count);
    void decreaseJoined(long id, int count);
    void soldOut(long id);
    void unSoldOut(long id);
    boolean sold(long id, int count);
}
