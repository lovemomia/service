package cn.momia.service.base.product.sku.impl;

import cn.momia.service.common.DbAccessService;
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkuServiceImpl extends DbAccessService implements SkuService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkuServiceImpl.class);
    private static final String[] SKU_FIELDS = { "id", "productId", "`desc`", "`type`", "properties", "prices", "`limit`", "needRealName", "stock", "unlockedStock", "lockedStock" };

    @Override
    public Sku get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Sku>() {
            @Override
            public Sku extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildSku(rs);
                return Sku.NOT_EXIST_SKU;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(SKU_FIELDS, ",");
    }

    private Sku buildSku(ResultSet rs) throws SQLException {
        try {
            Sku sku = new Sku();
            sku.setId(rs.getLong("id"));
            sku.setProductId(rs.getLong("productId"));
            sku.setDesc(rs.getString("desc"));
            sku.setType(rs.getInt("type"));
            sku.setProperties(parseProperties(sku.getId(), rs.getString("properties")));
            sku.setPrices(parsePrices(sku.getId(), rs.getString("prices")));
            sku.setLimit(rs.getInt("limit"));
            sku.setNeedRealName(rs.getBoolean("needRealName"));
            sku.setStock(rs.getInt("stock"));
            sku.setUnlockedStock(rs.getInt("unlockedStock"));
            sku.setLockedStock(rs.getInt("lockedStock"));

            return sku;
        }
        catch (Exception e) {
            LOGGER.error("fail to build sku: {}", rs.getLong("id"), e);
            return Sku.INVALID_SKU;
        }
    }

    private List<SkuProperty> parseProperties(long id, String propertiesJsonStr) {
        List<SkuProperty> properties = new ArrayList<SkuProperty>();
        JSONArray propertiesJson = JSON.parseArray(propertiesJsonStr);
        for (int i = 0; i < propertiesJson.size(); i++) {
            JSONObject propertyJson = propertiesJson.getJSONObject(i);
            properties.add(new SkuProperty(propertyJson));
        }

        return properties;
    }

    private List<SkuPrice> parsePrices(long id, String pricesJsonStr) {
        List<SkuPrice> prices = new ArrayList<SkuPrice>();
        JSONArray pricesJson = JSON.parseArray(pricesJsonStr);
        for (int i = 0; i < pricesJson.size(); i++) {
            JSONObject priceJson = pricesJson.getJSONObject(i);
            prices.add(new SkuPrice(priceJson));
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
                Sku sku = buildSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
    }

    @Override
    public Map<Long, List<Sku>> queryByProducts(Collection<Long> productIds) {
        final Map<Long, List<Sku>> skusOfProducts = new HashMap<Long, List<Sku>>();
        if (productIds == null || productIds.isEmpty()) return skusOfProducts;

        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE productId IN (" + StringUtils.join(productIds, ",") + ") AND status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Sku sku = buildSku(rs);
                if (!sku.exists()) return;
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
        if (isNoCeiling(id)) return true;

        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock-?, lockedStock=lockedStock+? WHERE id=? AND unlockedStock>=? AND stock-lockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[]{ count, count, id, count, count });

        return updateCount == 1;
    }

    private boolean isNoCeiling(long id) {
        String sql = "SELECT `type` FROM t_sku WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[]{id}, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getInt("type") == Sku.Type.NO_CEILING;
                return false;
            }
        });
    }

    @Override
    public boolean unlock(long id, int count) {
        if (isNoCeiling(id)) return true;

        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock+?, lockedStock=lockedStock-? WHERE id=? AND lockedStock>=? AND stock-unlockedStock>=? AND status=1";
        int updateCount = jdbcTemplate.update(sql, new Object[]{ count, count, id, count, count });

        return updateCount == 1;
    }
}
