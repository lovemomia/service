package cn.momia.service.user.sale;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface SaleUserCountService {
    int getUserCountBySaleCode(String saleCodes);
    void add(int userId, int saleId);
}
