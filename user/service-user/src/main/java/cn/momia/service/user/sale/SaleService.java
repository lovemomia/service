package cn.momia.service.user.sale;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface SaleService {
    long add(Sale sale);
    Sale getBySaleId(long id);
    Sale getBySaleCode(String saleCode);
    List<Sale> list(Collection<Long> saleIds);
    Sale getSaleByCode(String saleCode);
}
