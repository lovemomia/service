package cn.momia.service.product.base;

import cn.momia.service.base.Service;

import java.util.Collection;
import java.util.List;

public interface BaseProductService extends Service {
    BaseProduct get(long id);
    List<BaseProduct> get(Collection<Long> ids);

    long queryCount(String query);
    List<BaseProduct> query(int start, int count, String query);
    long queryWeekendCount(String query);
    List<BaseProduct> queryWeekend(int start, int count, String query);

    boolean join(long id, int count);
    boolean sold(long id, int count);
    boolean soldOut(long id);
    void unSoldOut(long id);
    void decreaseJoined(long id, int count);
}
