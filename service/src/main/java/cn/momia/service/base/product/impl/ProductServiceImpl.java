package cn.momia.service.base.product.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductImage;
import cn.momia.service.base.product.ProductQuery;
import cn.momia.service.base.product.ProductService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

public class ProductServiceImpl extends DbAccessService implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public Product get(long id) {
        Product product = getProduct(id);
        if (product.exists()) product.setImgs(getProductImgs(id));

        return product;
    }

    public Product getProduct(long id){
        String sql = "SELECT id, cityId, categoryId, title, cover, crowd, content, sales FROM t_product WHERE id=? AND status=1";

        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<Product>() {
            @Override
            public Product extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) return buildProduct(rs);
                return Product.NOT_EXIST_PRODUCT;
            }
        });
    }

    public Product buildProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setCityId(rs.getInt("cityId"));
        product.setCategoryId(rs.getInt("categoryId"));
        product.setTitle(rs.getString("title"));
        product.setCover(rs.getString("cover"));
        product.setCrowd(rs.getString("crowd"));
        product.setContent(parseContent(product.getId(), rs.getString("content")));
        product.setSales(rs.getInt("sales"));

        return product;
    }

    private JSONArray parseContent(long id, String content) throws SQLException {
        try {
            return JSON.parseArray(content);
        } catch (Exception e) {
            LOGGER.error("fail to parse product content, product id: {}", id);
            return new JSONArray();
        }
    }

    public List<ProductImage> getProductImgs(long id){
        final List<ProductImage> imgs = new ArrayList<ProductImage>();

        String sql = "SELECT url, width, height FROM t_product_img WHERE productId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { id }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(buildImage(rs));
            }
        });

        return imgs;
    }

    public ProductImage buildImage(ResultSet rs) throws SQLException {
        ProductImage img = new ProductImage();
        img.setUrl(rs.getString("url"));
        img.setWidth(rs.getInt("width"));
        img.setHeight(rs.getInt("height"));

        return img;
    }

    @Override
    public List<Product> get(List<Long> ids) {
        final List<Product> products = new ArrayList<Product>();
        if (ids.size() <= 0) return products;

        String sql = "SELECT id, cityId, categoryId, title, cover, crowd, content, sales FROM t_product WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1 ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                products.add(buildProduct(rs));
            }
        });

        return products;
    }

    @Override
    public List<Product> query(int start, int count, ProductQuery query) {
        final List<Product> products = new ArrayList<Product>();

        String sql = "SELECT id, cityId, categoryId, title, cover, crowd, content, sales FROM t_product WHERE status=1 AND " + query.toString() + " ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                products.add(buildProduct(rs));
            }
        });

        return products;
    }
}
