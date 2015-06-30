package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.Sku;
import cn.momia.admin.web.entity.SkuOther;
import cn.momia.admin.web.entity.SkuPrice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hoze on 15/6/15.
 */
public interface SkuService {
    public Sku get(int id);
    public List<Sku> getEntitys();
    public List<Sku> getEntitysByKey(int productId);
    public List<Sku> getQueryPages(int start_row,int end_row);
    public List<Sku> getEntities(List<Sku> skus);
    public int insert(Sku entity);
    public int update(Sku entity);
    public int update_price(Sku entity);
    public int delete(int id);
    public Sku formEntity(HttpServletRequest req, int id);
    public Sku formEntity2(HttpServletRequest req, int id);
    public String getPricesMap(String jsonPrices);
    public String getProperties(String jsonProperties);
    public SkuPrice getPricesMinValue(String jsonPrices);
}
