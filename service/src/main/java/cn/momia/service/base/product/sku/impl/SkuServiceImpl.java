package cn.momia.service.base.product.sku.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuPrice;
import cn.momia.service.base.product.sku.SkuProperty;
import cn.momia.service.base.product.sku.SkuService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkuServiceImpl extends DbAccessService implements SkuService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkuServiceImpl.class);
    private static final String[] SKU_FIELDS = { "id", "productId", "propertyValues", "price", "stock", "unlockedStock", "lockedStock" };

    @Override
    public List<Sku> get(List<Long> ids) {
        final List<Sku> skus = new ArrayList<Sku>();
        if (ids.isEmpty()) return skus;

        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                skus.add(buildSku(rs));
            }
        });

        return skus;
    }

    private String joinFields() {
        return StringUtils.join(SKU_FIELDS, ",");
    }

    private Sku buildSku(ResultSet rs) throws SQLException {
        Sku sku = new Sku();
        sku.setId(rs.getLong("id"));
        sku.setProductId(rs.getLong("productId"));
        sku.setProperties(parseProperties(sku.getId(), rs.getString("properties")));
        sku.setPrices(parsePrices(sku.getId(), rs.getString("prices")));
        sku.setStock(rs.getInt("stock"));
        sku.setUnlockedStock(rs.getInt("unlockedStock"));
        sku.setLockedStock(rs.getInt("lockedStock"));

        return sku;
    }

    private List<SkuProperty> parseProperties(long id, String propertiesJsonStr) {
        List<SkuProperty> properties = new ArrayList<SkuProperty>();

        try {
            JSONArray propertiesJson = JSON.parseArray(propertiesJsonStr);
            for (int i = 0; i < propertiesJson.size(); i++) {
                JSONObject propertyJson = propertiesJson.getJSONObject(i);
                properties.add(new SkuProperty(propertyJson));
            }
        } catch (Exception e) {
            LOGGER.error("fail to parse sku properties, sku id: {}", id);
        }

        return properties;
    }

    private List<SkuPrice> parsePrices(long id, String pricesJsonStr) {
        List<SkuPrice> prices = new ArrayList<SkuPrice>();

        try {
            JSONArray pricesJson = JSON.parseArray(pricesJsonStr);
            for (int i = 0; i < pricesJson.size(); i++) {
                JSONObject priceJson = pricesJson.getJSONObject(i);
                prices.add(new SkuPrice(priceJson));
            }
        } catch (Exception e) {
            LOGGER.error("fail to parse sku prices, sku id: {}", id);
        }

        return prices;
    }

    @Override
    public List<Sku> queryByProduct(long productId) {
        final List<Sku> skus = new ArrayList<Sku>();

        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE productId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { productId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                skus.add(buildSku(rs));
            }
        });

        return skus;
    }

    @Override
    public Map<Long, List<Sku>> queryByProducts(List<Long> productIds) {
        final Map<Long, List<Sku>> skusOfProducts = new HashMap<Long, List<Sku>>();
        if (productIds.isEmpty()) return skusOfProducts;

        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE productId IN (" + StringUtils.join(productIds, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Sku sku = buildSku(rs);
                List<Sku> skus = skusOfProducts.get(sku.getProductId());
                if (skus == null) {
                    skus = new ArrayList<Sku>();
                    skusOfProducts.put(sku.getProductId(), skus);
                }
                skus.add(sku);
            }
        });

        return skusOfProducts;
    }

    @Override
    public boolean lock(long id, int count) {
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock-?, lockedStock=lockedStock+? WHERE id=? AND unlockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, id, count });

        return updateCount == 1;
    }

    @Override
    public boolean unlock(long id, int count) {
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock+?, lockedStock=lockedStock-? WHERE id=? AND lockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, id, count });

        return updateCount == 1;
    }
}
