package cn.momia.service.base.product.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductImage;
import cn.momia.service.base.product.ProductQuery;
import cn.momia.service.base.product.ProductService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl extends DbAccessService implements ProductService {
    @Override
    public Product get(long id) {
        Product product = getProduct(id);
        if (product.exists()) product.setImgs(getProductImgs(id));

        return product;
    }

    public Product getProduct(long id){
        String sql = "SELECT id, categoryId, title, cover, content, sales FROM t_product WHERE id=? AND status=1";

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
        product.setCategoryId(rs.getInt("categoryId"));
        product.setTitle(rs.getString("title"));
        product.setCover(rs.getString("cover"));
        product.setContent(JSON.parseObject(rs.getString("content")));
        product.setSales(rs.getInt("sales"));

        return product;
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
    public List<Product> getByIds(List<Long> ids) {
        final List<Product> products = new ArrayList<Product>();

        String sql = "SELECT id, categoryId, title, cover, content, sales FROM t_product WHERE id IN (" + StringUtils.join(ids, ",") + ") AND status=1 ORDER BY addTime DESC";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                products.add(buildProduct(rs));
            }
        });

        return products;
    }

    @Override
    public List<Product> queryProducts(int start, int count, ProductQuery query) {
        // TODO use query
        final List<Product> products = new ArrayList<Product>();

        String sql = "SELECT id, categoryId, title, cover, content, sales FROM t_product WHERE status=1 ORDER BY addTime DESC LIMIT ?,?";
        jdbcTemplate.query(sql, new Object[] { start, count }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                products.add(buildProduct(rs));
            }
        });

        return products;
    }
}
