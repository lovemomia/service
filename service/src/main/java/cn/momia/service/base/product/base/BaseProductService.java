package cn.momia.service.base.product.base;

import java.util.Collection;
import java.util.List;

public interface BaseProductService {
    BaseProduct get(long id);
    List<BaseProduct> get(Collection<Long> ids);
    long queryCount(String query);
    List<BaseProduct> query(int start, int count, String query);
    boolean join(long id, int count);
    boolean sold(long id, int count);
    int getSales(long id);
    boolean soldOut(long id);
    void unSoldOut(long id);
    void decreaseJoined(long id, int count);
}
