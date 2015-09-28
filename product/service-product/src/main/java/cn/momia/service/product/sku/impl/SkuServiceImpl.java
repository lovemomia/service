package cn.momia.service.product.sku.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuPrice;
import cn.momia.service.product.sku.SkuProperty;
import cn.momia.service.product.sku.SkuService;
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
import java.util.List;

public class SkuServiceImpl extends DbAccessService implements SkuService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkuServiceImpl.class);
    private static final String[] SKU_FIELDS = { "id", "productId", "`desc`", "`type`", "anyTime", "startTime", "endTime", "properties", "prices", "`limit`", "needRealName", "stock", "unlockedStock", "lockedStock", "onlineTime", "offlineTime", "onWeekend", "needLeader", "leaderUserId", "forNewUser" };

    @Override
    public Sku get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE id=? AND status<>0";

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
            sku.setAnyTime(rs.getBoolean("anyTime"));
            sku.setStartTime(rs.getTimestamp("startTime"));
            sku.setEndTime(rs.getTimestamp("endTime"));
            sku.setProperties(parseProperties(rs.getString("properties")));
            sku.setPrices(parsePrices(rs.getString("prices")));
            sku.setLimit(rs.getInt("limit"));
            sku.setNeedRealName(rs.getBoolean("needRealName"));
            sku.setStock(rs.getInt("stock"));
            sku.setUnlockedStock(rs.getInt("unlockedStock"));
            sku.setLockedStock(rs.getInt("lockedStock"));
            sku.setOnlineTime(rs.getTimestamp("onlineTime"));
            sku.setOfflineTime(rs.getTimestamp("offlineTime"));
            sku.setOnWeekend(rs.getBoolean("onWeekend"));
            sku.setNeedLeader(rs.getBoolean("needLeader"));
            sku.setLeaderUserId(rs.getLong("leaderUserId"));
            sku.setForNewUser(rs.getBoolean("forNewUser"));

            return sku;
        }
        catch (Exception e) {
            LOGGER.error("fail to build sku: {}", rs.getLong("id"), e);
            return Sku.NOT_EXIST_SKU;
        }
    }

    private List<SkuProperty> parseProperties(String propertiesJsonStr) {
        List<SkuProperty> properties = new ArrayList<SkuProperty>();
        JSONArray propertiesJson = JSON.parseArray(propertiesJsonStr);
        for (int i = 0; i < propertiesJson.size(); i++) {
            JSONObject propertyJson = propertiesJson.getJSONObject(i);
            properties.add(new SkuProperty(propertyJson));
        }

        return properties;
    }

    private List<SkuPrice> parsePrices(String pricesJsonStr) {
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
        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE productId=? AND status=1 ORDER BY startTime ASC";
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
    public List<Sku> queryByProducts(Collection<Long> productIds) {
        if (productIds.isEmpty()) return new ArrayList<Sku>();

        final List<Sku> skus = new ArrayList<Sku>();
        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE productId IN (" + StringUtils.join(productIds, ",") + ") AND status=1 ORDER BY startTime ASC";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Sku sku = buildSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
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

    @Override
    public boolean addLeader(long userId, long productId, long id) {
        String sql = "UPDATE t_sku SET leaderUserId=? WHERE id=? AND productId=? AND status=1 AND (leaderUserId<=0 OR leaderUserId=?)";
        return jdbcTemplate.update(sql, new Object[] { userId, id, productId, userId }) == 1;
    }

    @Override
    public long queryCountOfLedSkus(long userId) {
        String sql = "SELECT COUNT(1) FROM t_sku WHERE leaderUserId=? AND status=1 AND startTime>NOW()";

        return jdbcTemplate.query(sql, new Object[] { userId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<Sku> queryLedSkus(long userId, int start, int count) {
        final List<Sku> skus = new ArrayList<Sku>();
        String sql = "SELECT " + joinFields() + " FROM t_sku WHERE leaderUserId=? AND status=1 AND startTime>NOW() ORDER BY startTime ASC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { userId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Sku sku = buildSku(rs);
                if (sku.exists()) skus.add(sku);
            }
        });

        return skus;
    }
}
