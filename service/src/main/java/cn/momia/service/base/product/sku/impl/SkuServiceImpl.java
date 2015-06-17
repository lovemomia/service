package cn.momia.service.base.product.sku.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuProperty;
import cn.momia.service.base.product.sku.SkuPropertyName;
import cn.momia.service.base.product.sku.SkuPropertyValue;
import cn.momia.service.base.product.sku.SkuService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkuServiceImpl extends DbAccessService implements SkuService {
    private Map<Long, SkuPropertyName> skuPropertyNameCache = new HashMap<Long, SkuPropertyName>();
    private Map<Long, SkuPropertyValue> skuPropertyValueCache = new HashMap<Long, SkuPropertyValue>();

    public void init() {
        cachePropertyNames();
        cachePropertyValues();
    }

    private void cachePropertyNames() {
        String sql = "SELECT id, categoryId, name FROM t_sku_property_name WHERE status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SkuPropertyName propertyName = buildPropertyName(rs);
                skuPropertyNameCache.put(propertyName.getId(), propertyName);
            }
        });
    }

    private SkuPropertyName buildPropertyName(ResultSet rs) throws SQLException {
        SkuPropertyName propertyName = new SkuPropertyName();
        propertyName.setId(rs.getLong("id"));
        propertyName.setCategoryId(rs.getInt("categoryId"));
        propertyName.setName(rs.getString("name"));

        return propertyName;
    }

    private void cachePropertyValues() {
        String sql = "SELECT id, nameId, value FROM t_sku_property_value WHERE status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SkuPropertyValue propertyValue = buildPropertyValue(rs);
                skuPropertyValueCache.put(propertyValue.getId(), propertyValue);
            }
        });
    }

    private SkuPropertyValue buildPropertyValue(ResultSet rs) throws SQLException {
        SkuPropertyValue propertyValue = new SkuPropertyValue();
        propertyValue.setId(rs.getLong("id"));
        propertyValue.setNameId(rs.getLong("nameId"));
        propertyValue.setValue(rs.getString("value"));

        return propertyValue;
    }

    @Override
    public List<Sku> queryByProduct(long productId) {
        final List<Sku> skus = new ArrayList<Sku>();

        String sql = "SELECT id, productId, propertyValues, price, stock, unlockedStock, lockedStock FROM t_sku WHERE productId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { productId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                skus.add(buildSku(rs));
            }
        });

        return skus;
    }

    private Sku buildSku(ResultSet rs) throws SQLException {
        Sku sku = new Sku();
        sku.setId(rs.getLong("id"));
        sku.setProductId(rs.getLong("productId"));
        sku.setProperties(parseProperties(rs.getString("propertyValues")));
        sku.setPrice(rs.getFloat("price"));
        sku.setStock(rs.getInt("stock"));
        sku.setUnlockedStock(rs.getInt("unlockedStock"));
        sku.setLockedStock(rs.getInt("lockedStock"));

        return sku;
    }

    private List<SkuProperty> parseProperties(String propertyValues) {
        List<SkuProperty> properties = new ArrayList<SkuProperty>();

        JSONArray propertiesJson = JSON.parseArray(propertyValues);
        for (int i = 0; i < propertiesJson.size(); i++) {
            JSONObject propertyObject = propertiesJson.getJSONObject(i);
            int type = propertyObject.getInteger("type");
            switch (type) {
                case SkuProperty.Type.REF:
                    SkuPropertyValue propertyValue = skuPropertyValueCache.get(propertyObject.getLong("valueid"));
                    properties.add(new SkuProperty(skuPropertyNameCache.get(propertyValue.getNameId()).getName(), propertyValue.getValue()));
                    break;
                case SkuProperty.Type.VALUE:
                    properties.add(new SkuProperty(skuPropertyNameCache.get(propertyObject.getLong("nameid")).getName(), propertyObject.getString("value")));
                    break;
                default:
                    break;
            }
        }

        return properties;
    }

    @Override
    public Map<Long, List<Sku>> queryByProducts(List<Long> productIds) {
        final Map<Long, List<Sku>> skusOfProducts = new HashMap<Long, List<Sku>>();
        if (productIds.size() <= 0) return skusOfProducts;

        String sql = "SELECT id, productId, propertyValues, price, stock, unlockedStock, lockedStock FROM t_sku WHERE productId IN (" + StringUtils.join(productIds, ",") + ") AND status=1";
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
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock-?, lockedStock=lockedStock+? WHERE skuId=? AND unlockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, id, count });

        return updateCount == 1;
    }

    @Override
    public boolean unlock(long id, int count) {
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock+?, lockedStock=lockedStock-? WHERE skuId=? AND lockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, id, count });

        return updateCount == 1;
    }
}
