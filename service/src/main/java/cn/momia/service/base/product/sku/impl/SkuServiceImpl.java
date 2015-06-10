package cn.momia.service.base.product.sku.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SkuServiceImpl extends DbAccessService implements SkuService {
    @Override
    public long add(Sku sku) {
        long productId = addProduct(sku);
        addProductImages(sku);
        return addSku(sku);
    }

    @Override
    public boolean update(Sku sku) {
        return false;
    }

    private void addProductImages(Sku sku) {
    }

    private long addProduct(Sku sku) {
        return 0;
    }

    private long addSku(Sku sku) {
        return 0;
    }

    @Override
    public Sku get(final long id) {
        JSONObject skuObject = new JSONObject();

        long productId = querySkuAndProductId(id, skuObject);
        if (productId == 0) return Sku.NOT_EXIST_SKU.NOT_EXIST_SKU;

        JSONObject productObject = queryProduct(productId);
        if (productObject == null) return Sku.NOT_EXIST_SKU;

        productObject.put("imgs", queryProductImgs(productId));
        skuObject.put("product", productObject);

        return new Sku(skuObject);
    }

    @Override
    public List<Sku> queryByProduct(long productId) {
        return null;
    }

    private long querySkuAndProductId(long id, final JSONObject skuObject) {
        String skuSql = "SELECT id, productId, propertyValues, price, stock FROM t_sku WHERE id=? AND status=1";

        return jdbcTemplate.query(skuSql, new Object[] { id }, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) return 0L;

                skuObject.put("id", rs.getLong("id"));
                Iterable<String> values = Splitter.on(",").omitEmptyStrings().trimResults().split(rs.getString("propertyValues"));
                JSONArray propertyValues = new JSONArray();
                for (String value : values) {
                    propertyValues.add(Long.valueOf(value));
                }
                skuObject.put("propertyValues", propertyValues);
                skuObject.put("price", rs.getFloat("price"));
                skuObject.put("stock", rs.getInt("stock"));

                return rs.getLong("productId");
            }
        });
    }

    private JSONObject queryProduct(long productId) {
        String productSql = "SELECT id, category, userId, title, content, sales FROM t_product WHERE id=? AND status=1";

        return jdbcTemplate.query(productSql, new Object[] { productId }, new ResultSetExtractor<JSONObject>() {
            @Override
            public JSONObject extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) return null;

                JSONObject productObject = JSONObject.parseObject(rs.getString("content"));
                if (productObject == null) productObject = new JSONObject();
                productObject.put("id", rs.getLong("id"));
                productObject.put("category", rs.getInt("category"));
                productObject.put("userId", rs.getLong("userId"));
                productObject.put("title", rs.getString("title"));
                productObject.put("sales", rs.getInt("sales"));

                return productObject;
            }
        });
    }

    private JSONArray queryProductImgs(long productId) {
        final JSONArray imgs = new JSONArray();

        String imgSql = "SELECT url, width, height FROM t_product_img WHERE productId=? AND status=1";
        jdbcTemplate.query(imgSql, new Object[] { productId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                JSONObject image = new JSONObject();
                image.put("url", rs.getString("url"));
                image.put("width", rs.getInt("width"));
                image.put("height", rs.getInt("height"));

                imgs.add(image);
            }
        });

        return imgs;
    }

    @Override
    public boolean lock(long skuId, int count) {
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock-?, lockedStock=lockedStock+? WHERE skuId=? AND unlockedStock>=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, skuId, count });

        return updateCount > 0;
    }

    @Override
    public boolean unlock(long skuId, int count) {
        String sql = "UPDATE t_sku SET unlockedStock=unlockedStock+?, lockedStock=lockedStock-? WHERE skuId=? AND lockedStock>=?";
        int updateCount = jdbcTemplate.update(sql, new Object[] { count, count, skuId, count });

        return updateCount > 0;
    }
}
