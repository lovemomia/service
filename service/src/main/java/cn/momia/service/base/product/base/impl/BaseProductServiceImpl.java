package cn.momia.service.base.product.base.impl;

import cn.momia.service.common.DbAccessService;
import cn.momia.service.base.product.base.BaseProduct;
import cn.momia.service.base.product.base.BaseProductService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseProductServiceImpl extends DbAccessService implements BaseProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProductServiceImpl.class);
    private static final String[] PRODUCT_FIELDS = {"id", "cityId", "title", "cover", "crowd", "placeId", "content", "sales"};

    @Override
    public BaseProduct get(long id) {
        String sql = "SELECT " + joinFields() + " FROM t_product WHERE id=? AND status=1";

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

    public BaseProduct buildBaseProduct(ResultSet rs) throws SQLException {
        try {
            BaseProduct baseProduct = new BaseProduct();
            baseProduct.setId(rs.getLong("id"));
            baseProduct.setCityId(rs.getInt("cityId"));
            baseProduct.setTitle(rs.getString("title"));
            baseProduct.setCover(rs.getString("cover"));
            baseProduct.setCrowd(rs.getString("crowd"));
            baseProduct.setPlaceId(rs.getLong("placeId"));
            baseProduct.setContent(JSON.parseArray(rs.getString("content")));
            baseProduct.setSales(rs.getInt("sales"));

            return baseProduct;
        }
        catch (Exception e) {
            LOGGER.error("fail to build base product: {}", rs.getLong("id"), e);
            return BaseProduct.INVALID_BASEPRODUCT;
        }
    }

    @Override
    public List<BaseProduct> get(List<Long> ids) {
        final List<BaseProduct> baseProducts = new ArrayList<BaseProduct>();
        if (ids.isEmpty()) return baseProducts;

        String sql = "SELECT " + joinFields() + " FROM t_product WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1 ORDER BY addTime DESC";
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
    public List<BaseProduct> query(int start, int count, String query) {
        final List<BaseProduct> baseProducts = new ArrayList<BaseProduct>();

        String sql = "SELECT " + joinFields() + " FROM t_product WHERE status=1 AND " + query + " ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[]{start, count}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BaseProduct baseProduct = buildBaseProduct(rs);
                if (baseProduct.exists()) baseProducts.add(baseProduct);
            }
        });

        return baseProducts;
    }
}
