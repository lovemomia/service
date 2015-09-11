package cn.momia.service.product.base.impl;

import cn.momia.common.service.DbAccessService;
import cn.momia.service.product.base.BaseProduct;
import cn.momia.service.product.base.BaseProductService;
import cn.momia.service.product.base.ProductSort;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseProductServiceImpl extends DbAccessService implements BaseProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProductServiceImpl.class);

    private static final Splitter TAGS_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final Splitter PLACES_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final int MAX_TAG_COUNT = 3;
    private static final String[] PRODUCT_FIELDS = { "id", "cityId", "tags", "title", "abstracts", "cover", "thumb", "crowd", "placeId", "places", "content", "joined", "sales", "onlineTime", "offlineTime", "status" };

    private Map<Integer, String> tagsCache;

    public void init() {
        tagsCache = new HashMap<Integer, String>();
        String sql = "SELECT id, name FROM t_tag WHERE status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                tagsCache.put(rs.getInt("id"), rs.getString("name"));
            }
        });
    }

    @Override
    public BaseProduct get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_product WHERE id=? AND status<>0";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<BaseProduct>() {
            @Override
            public BaseProduct extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildBaseProduct(rs);
                return BaseProduct.NOT_EXIST_BASEPRODUCT;
            }
        });
    }

    private String joinFields() {
        return StringUtils.join(PRODUCT_FIELDS, ",");
    }

    private BaseProduct buildBaseProduct(ResultSet rs) throws SQLException {
        try {
            BaseProduct baseProduct = new BaseProduct();
            baseProduct.setId(rs.getLong("id"));
            baseProduct.setCityId(rs.getInt("cityId"));
            baseProduct.setTags(parseTags(rs.getString("tags")));
            baseProduct.setTitle(rs.getString("title"));
            baseProduct.setAbstracts(rs.getString("abstracts"));
            baseProduct.setCover(rs.getString("cover"));
            baseProduct.setThumb(rs.getString("thumb"));
            baseProduct.setCrowd(rs.getString("crowd"));
            baseProduct.setPlaces(parsePlaces(rs.getInt("placeId"), rs.getString("places")));
            baseProduct.setContent(JSON.parseArray(rs.getString("content")));
            baseProduct.setJoined(rs.getInt("joined"));
            baseProduct.setSales(rs.getInt("sales"));
            baseProduct.setOnlineTime(rs.getTimestamp("onlineTime"));
            baseProduct.setOfflineTime(rs.getTimestamp("offlineTime"));
            baseProduct.setStatus(rs.getInt("status"));

            return baseProduct;
        }
        catch (Exception e) {
            LOGGER.error("fail to build base product: {}", rs.getLong("id"), e);
            return BaseProduct.INVALID_BASEPRODUCT;
        }
    }

    private List<String> parseTags(String tagsStr) {
        List<String> tags = new ArrayList<String>();
        int count = 0;
        for (String tagStr : TAGS_SPLITTER.split(tagsStr)) {
            if (++count > MAX_TAG_COUNT) break;
            String tagName = tagsCache.get(Integer.valueOf(tagStr));
            if (!StringUtils.isBlank(tagName)) tags.add(tagName);
        }

        return tags;
    }


    private Set<Integer> parsePlaces(int placeId, String placesStr) {
        Set<Integer> places = new HashSet<Integer>();
        if (!StringUtils.isBlank(placesStr)) {
            for (String id : PLACES_SPLITTER.split(placesStr)) {
                places.add(Integer.valueOf(id));
            }
        }

        if (places.isEmpty()) places.add(placeId);

        return places;
    }

    @Override
    public List<BaseProduct> get(Collection<Long> ids) {
        List<BaseProduct> baseProducts = new ArrayList<BaseProduct>();
        if (ids == null || ids.isEmpty()) return baseProducts;

        final Map<Long, BaseProduct> baseProductsMap = new HashMap<Long, BaseProduct>();
        String sql = "SELECT " + joinFields() + " FROM t_product WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status<>0 ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BaseProduct baseProduct = buildBaseProduct(rs);
                if (baseProduct.exists()) baseProductsMap.put(baseProduct.getId(), baseProduct);
            }
        });

        for (long id : ids) {
            BaseProduct baseProduct = baseProductsMap.get(id);
            if (baseProduct != null) baseProducts.add(baseProduct);
        }

        return baseProducts;
    }

    @Override
    public String getDetail(long id) {
        String sql = "SELECT detail FROM t_product WHERE id=? AND status<>0";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getString("detail");
                return "";
            }
        });
    }

    @Override
    public long queryCount(int cityId) {
        String sql = "SELECT COUNT(1) FROM t_product WHERE status=1 " +
                "AND onlineTime<=NOW() AND offlineTime>NOW() " +
                "AND (cityId=? OR cityId=0)";

        return jdbcTemplate.query(sql, new Object[] { cityId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<BaseProduct> query(int cityId, int start, int count, ProductSort sort) {
        final List<BaseProduct> baseProducts = new ArrayList<BaseProduct>();

        String sql = "SELECT " + joinFields() + " FROM t_product WHERE status=1 " +
                "AND onlineTime<=NOW() AND offlineTime>NOW() " +
                "AND (cityId=? OR cityId=0) " +
                "ORDER BY " + sort.toString() + " LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { cityId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BaseProduct baseProduct = buildBaseProduct(rs);
                if (baseProduct.exists()) baseProducts.add(baseProduct);
            }
        });

        return baseProducts;
    }

    @Override
    public long queryCountByWeekend(int cityId) {
        String sql = "SELECT COUNT(DISTINCT A.id) " +
                "FROM t_product A INNER JOIN t_sku B ON A.id=B.productId " +
                "WHERE A.status=1 AND A.onlineTime<=NOW() AND A.offlineTime>NOW() AND A.soldOut=0 " +
                "AND B.status=1 AND B.onlineTime<=NOW() AND B.offlineTime>NOW() " +
                "AND (B.type=1 OR B.unlockedStock>0) AND B.onWeekend=1 " +
                "AND (cityId=? OR cityId=0)";

        return jdbcTemplate.query(sql, new Object[] { cityId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<BaseProduct> queryByWeekend(int cityId, int start, int count) {
        String sql = "SELECT DISTINCT A.id " +
                "FROM t_product A INNER JOIN t_sku B ON A.id=B.productId " +
                "WHERE A.status=1 AND A.onlineTime<=NOW() AND A.offlineTime>NOW() AND A.soldOut=0 " +
                "AND B.status=1 AND B.onlineTime<=NOW() AND B.offlineTime>NOW() " +
                "AND (B.type=1 OR B.unlockedStock>0) AND B.onWeekend=1 " +
                "AND (cityId=? OR cityId=0) " +
                "ORDER BY B.startTime ASC LIMIT ?,?";
        final List<Long> ids = new ArrayList<Long>();
        jdbcTemplate.query(sql, new Object[] { cityId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ids.add(rs.getLong("id"));
            }
        });

        return get(ids);
    }

    @Override
    public long queryCountByMonth(int cityId, String currentMonth, String nextMonth) {
        String sql = "SELECT COUNT(DISTINCT A.id) " +
                "FROM t_product A INNER JOIN t_sku B ON A.id=B.productId " +
                "WHERE A.status=1 AND A.onlineTime<=NOW() AND A.offlineTime>NOW() AND A.soldOut=0 " +
                "AND B.status=1 AND B.onlineTime<=NOW() AND B.offlineTime>NOW() " +
                "AND (B.type=1 OR B.unlockedStock>0) AND B.startTime>=? AND B.endTime<? " +
                "AND (cityId=? OR cityId=0)";

        return jdbcTemplate.query(sql, new Object[] { currentMonth, nextMonth, cityId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<BaseProduct> queryByMonth(int cityId, String currentMonth, String nextMonth) {
        String sql = "SELECT DISTINCT A.id " +
                "FROM t_product A INNER JOIN t_sku B ON A.id=B.productId " +
                "WHERE A.status=1 AND A.onlineTime<=NOW() AND A.offlineTime>NOW() AND A.soldOut=0 " +
                "AND B.status=1 AND B.onlineTime<=NOW() AND B.offlineTime>NOW() " +
                "AND (B.type=1 OR B.unlockedStock>0) AND B.startTime>=? AND B.endTime<? " +
                "AND (cityId=? OR cityId=0) ORDER BY B.startTime ASC";
        final List<Long> ids = new ArrayList<Long>();
        jdbcTemplate.query(sql, new Object[] { currentMonth, nextMonth, cityId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ids.add(rs.getLong("id"));
            }
        });

        return get(ids);
    }

    @Override
    public long queryCountNeedLeader(int cityId) {
        String sql = "SELECT COUNT(DISTINCT A.id) " +
                "FROM t_product A INNER JOIN t_sku B ON A.id=B.productId " +
                "WHERE A.status=1 AND A.onlineTime<=NOW() AND A.offlineTime>NOW() AND A.soldOut=0 " +
                "AND B.status=1 AND B.onlineTime<=NOW() AND B.offlineTime>NOW() AND B.startTime>NOW() " +
                "AND (B.type=1 OR B.unlockedStock>0) AND B.needLeader=1 AND B.leaderUserId<=0 " +
                "AND (cityId=? OR cityId=0)";

        return jdbcTemplate.query(sql, new Object[] { cityId }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<BaseProduct> queryNeedLeader(int cityId, int start, int count) {
        String sql = "SELECT DISTINCT A.id " +
                "FROM t_product A INNER JOIN t_sku B ON A.id=B.productId " +
                "WHERE A.status=1 AND A.onlineTime<=NOW() AND A.offlineTime>NOW() AND A.soldOut=0 " +
                "AND B.status=1 AND B.onlineTime<=NOW() AND B.offlineTime>NOW() AND B.startTime>NOW() " +
                "AND (B.type=1 OR B.unlockedStock>0) AND B.needLeader=1 AND B.leaderUserId<=0 " +
                "AND (cityId=? OR cityId=0) " +
                "ORDER BY B.startTime ASC LIMIT ?,?";
        final List<Long> ids = new ArrayList<Long>();
        jdbcTemplate.query(sql, new Object[] { cityId, start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ids.add(rs.getLong("id"));
            }
        });

        return get(ids);
    }

    @Override
    public void join(long id, int count) {
        String sql = "UPDATE t_product SET joined=joined+? WHERE id=? AND status<>0";
        jdbcTemplate.update(sql, new Object[] { count, id });
    }

    @Override
    public void decreaseJoined(long id, int count) {
        String sql = "UPDATE t_product SET joined=joined-? WHERE id=? AND joined>=? AND status<>0";
        jdbcTemplate.update(sql, new Object[] { count, id, count });
    }

    @Override
    public void soldOut(long id) {
        String sql = "UPDATE t_product SET soldOut=1 WHERE id=? AND status<>0";
        jdbcTemplate.update(sql, new Object[] { id });
    }

    @Override
    public void unSoldOut(long id) {
        String sql = "UPDATE t_product SET soldOut=0 WHERE id=? AND soldOut=1 AND status<>0";
        jdbcTemplate.update(sql, new Object[] { id });
    }

    @Override
    public boolean sold(long id, int count) {
        String sql = "UPDATE t_product SET sales=sales+? WHERE id=? AND status<>0";

        return jdbcTemplate.update(sql, new Object[] { count, id }) == 1;
    }
}
