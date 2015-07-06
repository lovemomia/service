package cn.momia.service.base.product.base;

import java.util.List;

public interface BaseProductService {
    BaseProduct get(long id);
    List<BaseProduct> get(List<Long> ids);
    long queryCount(String query);
    List<BaseProduct> query(int start, int count, String query);
    boolean sold(long id, int count);
}
