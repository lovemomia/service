package cn.momia.service.base.product.base.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.base.product.base.BaseProduct;
import cn.momia.service.base.product.base.BaseProductService;
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
import java.util.List;
import java.util.Map;

public class BaseProductServiceImpl extends DbAccessService implements BaseProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProductServiceImpl.class);
    private static final Splitter TAGS_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final int MAX_TAG_COUNT = 3;
    private static final String[] PRODUCT_FIELDS = { "id", "cityId", "tags", "title", "abstracts", "cover", "thumb", "crowd", "placeId", "content", "joined", "sales", "soldOut", "startTime", "endTime" };

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
        String sql = "SELECT " + joinFields() + " FROM t_product WHERE id=? AND status=1 AND startTime<=NOW() AND endTime>=NOW()";

        return jdbcTemplate.query(sql, new Object[]{id}, new ResultSetExtractor<BaseProduct>() {
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
            baseProduct.setPlaceId(rs.getLong("placeId"));
            baseProduct.setContent(JSON.parseArray(rs.getString("content")));
            baseProduct.setJoined(rs.getInt("joined"));
            baseProduct.setSales(rs.getInt("sales"));
            baseProduct.setSoldOut(rs.getBoolean("soldOut"));
            baseProduct.setStartTime(rs.getTimestamp("startTime"));
            baseProduct.setEndTime(rs.getTimestamp("endTime"));

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

    @Override
    public List<BaseProduct> get(Collection<Long> ids) {
        final List<BaseProduct> baseProducts = new ArrayList<BaseProduct>();
        if (ids == null || ids.isEmpty()) return baseProducts;

        String sql = "SELECT " + joinFields() + " FROM t_product WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1 AND startTime<=NOW() AND endTime>=NOW() ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BaseProduct baseProduct = buildBaseProduct(rs);
                if (baseProduct.exists()) baseProducts.add(baseProduct);
            }
        });

        return baseProducts;
    }

    @Override
    public long queryCount(String query) {
        String sql = "SELECT COUNT(1) FROM t_product WHERE status=1 AND startTime<=NOW() AND endTime>=NOW() AND " + query;

        return jdbcTemplate.query(sql, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? rs.getLong(1) : 0;
            }
        });
    }

    @Override
    public List<BaseProduct> query(int start, int count, String query) {
        final List<BaseProduct> baseProducts = new ArrayList<BaseProduct>();

        String sql = "SELECT " + joinFields() + " FROM t_product WHERE status=1 AND startTime<=NOW() AND endTime>=NOW() AND " + query + " ORDER BY ordinal DESC, soldOut ASC, addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[]{start, count}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BaseProduct baseProduct = buildBaseProduct(rs);
                if (baseProduct.exists()) baseProducts.add(baseProduct);
            }
        });

        return baseProducts;
    }

    @Override
    public boolean join(long id, int count) {
        String sql = "UPDATE t_product SET joined=joined+? WHERE id=? AND status=1";

        return jdbcTemplate.update(sql, new Object[] { count, id }) == 1;
    }

    @Override
    public boolean sold(long id, int count) {
        String sql = "UPDATE t_product SET sales=sales+? WHERE id=? AND status=1";

        return jdbcTemplate.update(sql, new Object[] { count, id }) == 1;
    }

    @Override
    public int getSales(long id) {
        String sql = "SELECT sales FROM t_product WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[]{id}, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return rs.getInt("sales");
                return Integer.MAX_VALUE;
            }
        });
    }

    @Override
    public boolean soldOut(long id) {
        String sql = "UPDATE t_product SET soldOut=1 WHERE id=? AND status=1";

        return jdbcTemplate.update(sql, new Object[] { id }) == 1;
    }

    @Override
    public void unSoldOut(long id) {
        String sql = "UPDATE t_product SET soldOut=0 WHERE id=? AND soldOut=1 AND status=1";
        jdbcTemplate.update(sql, new Object[] { id });
    }

    @Override
    public void decreaseJoined(long id, int count) {
        String sql = "UPDATE t_product SET joined=joined-? WHERE id=? AND joined>=? AND status=1";
        jdbcTemplate.update(sql, new Object[] { count, id, count });
    }
}
