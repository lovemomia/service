package cn.momia.service.base.product.sku.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuProperty;
import cn.momia.service.base.product.sku.SkuPropertyValue;
import cn.momia.service.base.product.sku.SkuService;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkuServiceImpl extends DbAccessService implements SkuService {
    private Map<Long, SkuProperty> skuPropertyCache = new HashMap<Long, SkuProperty>();

    public void init() {
        String sql = "SELECT id, categoryId, name FROM t_sku_property WHERE status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SkuProperty property = buildProperty(rs);
                skuPropertyCache.put(property.getId(), property);
            }
        });
    }

    private SkuProperty buildProperty(ResultSet rs) throws SQLException {
        SkuProperty property = new SkuProperty();
        property.setId(rs.getLong("id"));
        property.setCategoryId(rs.getInt("categoryId"));
        property.setName(rs.getString("name"));

        return property;
    }

    @Override
    public List<Sku> queryByProduct(long productId) {
        final List<Sku> skus = new ArrayList<Sku>();
        final List<String> skuPropertyValuesList = new ArrayList<String>();

        String sql = "SELECT id, productId, propertyValues, price, stock, unlockedStock, lockedStock FROM t_sku WHERE productId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { productId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                skus.add(buildSku(rs));
                skuPropertyValuesList.add(rs.getString("propertyValues"));
            }
        });

        for (int i = 0; i < skus.size(); i++) {
            skus.get(i).setProperties(getSkuProperties(skuPropertyValuesList.get(i)));
        }

        return skus;
    }

    private Sku buildSku(ResultSet rs) throws SQLException {
        Sku sku = new Sku();
        sku.setId(rs.getLong("id"));
        sku.setProductId(rs.getLong("productId"));
        sku.setPrice(rs.getFloat("price"));
        sku.setStock(rs.getInt("stock"));
        sku.setUnlockedStock(rs.getInt("unlockedStock"));
        sku.setLockedStock(rs.getInt("lockedStock"));

        return sku;
    }

    private List<Pair<SkuProperty, SkuPropertyValue>> getSkuProperties(String skuPropertyValues) {
        final List<Pair<SkuProperty, SkuPropertyValue>> properties = new ArrayList<Pair<SkuProperty, SkuPropertyValue>>();

        for (String value : Splitter.on(",").trimResults().omitEmptyStrings().split(skuPropertyValues)) {
            String sql = "SELECT id, propertyId, value FROM t_sku_property_value WHERE id=? AND status=1";
            jdbcTemplate.query(sql, new Object[] { Long.valueOf(value) }, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    SkuPropertyValue propertyValue = buildPropertyValue(rs);
                    SkuProperty property = skuPropertyCache.get(propertyValue.getPropertyId());
                    if (property == null) return;
                    properties.add(new MutablePair<SkuProperty, SkuPropertyValue>(property, propertyValue));
                }
            });
        }

        return properties;
    }

    private SkuPropertyValue buildPropertyValue(ResultSet rs) throws SQLException {
        SkuPropertyValue propertyValue = new SkuPropertyValue();
        propertyValue.setId(rs.getLong("id"));
        propertyValue.setPropertyId(rs.getLong("propertyId"));
        propertyValue.setValue(rs.getString("value"));

        return propertyValue;
    }

    @Override
    public boolean lock(long id, int count) {
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock-?, lockedStock=lockedStock+? WHERE skuId=? AND unlockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, id, count });

        return updateCount > 0;
    }

    @Override
    public boolean unlock(long id, int count) {
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock+?, lockedStock=lockedStock-? WHERE skuId=? AND lockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, id, count });

        return updateCount > 0;
    }
}
