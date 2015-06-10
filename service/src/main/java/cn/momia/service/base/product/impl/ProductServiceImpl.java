package cn.momia.service.base.product.impl;

import cn.momia.service.base.DbAccessService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuProperty;
import cn.momia.service.base.product.sku.SkuPropertyValue;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl extends DbAccessService implements ProductService {

    public long addProduct(final Product product){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator(){

            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                String sql = "insert into t_product(category, userId, title, content, sales, addTime) values(?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, product.getCategory());
                ps.setLong(2, product.getUserId());
                ps.setString(3, product.getTitle());
                ps.setString(4, product.getContent());
                ps.setInt(5, product.getSales());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void addSku(final long productId, Product product){
        List<Sku> skus = new ArrayList<Sku>();
        skus = product.getSkus();

       for (int i = 0; i< skus.size(); i++) {
           final Sku sku = skus.get(i);
           jdbcTemplate.update(new PreparedStatementCreator() {

               @Override
               public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                   String sql = "insert into t_sku(productId, propertyValues, price, stock, lockedStock, unlockedStock, addTime) values(?, ?, ?, ?, ?, ?,NOW())";
                   PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                   ps.setLong(1, productId);
                   ps.setString(2, sku.getPropertyValues());
                   ps.setFloat(3, sku.getPrice());
                   ps.setInt(4, sku.getStock());
                   ps.setInt(5,sku.getLockedStock());
                   ps.setInt(6,sku.getUnlockedStock());
                   return ps;
               }
           });

           for (int j = 0; j < sku.getProperties().size(); j++) {
               SkuProperty skuProperty = sku.getProperties().get(j).getKey();
               long propertyId = addSkuProperty(skuProperty);
               SkuPropertyValue skuPropertyValue = sku.getProperties().get(j).getValue();
               addSkuPropertyValue(skuPropertyValue, propertyId);
           }
       }

    }

    public long addSkuProperty(final SkuProperty skuProperty){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_sku_property(categoryId,  name, addTime) VALUES (?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, skuProperty.getCategoryId());
                ps.setString(2, skuProperty.getName());
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public long addSkuPropertyValue(final SkuPropertyValue skuPropertyValue, final long propertyId){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO t_sku_property_value(propertyId,  value, addTime) VALUES (?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, propertyId);
                ps.setString(2, skuPropertyValue.getValue());
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
    @Override
    public long add(Product product) {

        long productId = addProduct(product);
        addSku(productId,product);
        return  productId;
    }

    @Override
    public boolean update(Product product) {
        return false;
    }

    @Override
    public Product get(long productId) {
        return null;
    }
}
