package cn.momia.service.base.product.impl;

import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuProperty;
import cn.momia.service.base.product.sku.SkuPropertyValue;
import com.alibaba.fastjson.JSONObject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by ysm on 15-6-10.
 */
public class ProductServiceImplTest {
    private ProductServiceImpl productService = new ProductServiceImpl();
    public static final String url = "jdbc:mysql://120.55.102.12:3306/tongqu?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "tongqu";
    public static final String password = "Tongqu!@#456";

    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    public JdbcTemplate jdbcTemplate = new JdbcTemplate();
    public void DB()throws Exception{
        dataSource.setDriverClass(name);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setMaxPoolSize(30);
        dataSource.setMaxIdleTime(7200);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setIdleConnectionTestPeriod(5);
        dataSource.setPreferredTestQuery("SELECT 1");
        dataSource.setCheckoutTimeout(1800000);
        jdbcTemplate.setDataSource(dataSource);
        productService.setJdbcTemplate(jdbcTemplate);
    }


    @org.testng.annotations.Test
    public void testAdd() throws Exception {
        DB();

        SkuPropertyValue skuPropertyValue = new SkuPropertyValue();
        SkuProperty skuProperty = new SkuProperty();

        skuProperty.setCategoryId(1);
        skuProperty.setName("time address");

        skuPropertyValue.setValue("morning 8:00AM");

        List<Sku> skus = new ArrayList<Sku>();

        Sku sku1 = new Sku();
        sku1.setPrice(9.9f);
        sku1.setPropertyValues("morning 8:00AM");
        sku1.setStock(5);
        sku1.setLockedStock(0);
        sku1.setUnlockedStock(sku1.getLockedStock());

        Sku sku2 = new Sku();
        sku2.setPrice(9.9f);
        sku2.setPropertyValues("afternoon 8:00PM");
        sku2.setStock(5);
        sku2.setLockedStock(0);
        sku2.setUnlockedStock(sku2.getLockedStock());

        List<Pair<SkuProperty, SkuPropertyValue>> properties = new ArrayList<Pair<SkuProperty, SkuPropertyValue>>();
        Pair<SkuProperty, SkuPropertyValue> pair1 = new ImmutablePair<SkuProperty, SkuPropertyValue>(skuProperty,skuPropertyValue);
        Pair<SkuProperty, SkuPropertyValue> pair2 = new ImmutablePair<SkuProperty, SkuPropertyValue>(skuProperty,skuPropertyValue);

        properties.add(pair1);
        properties.add(pair2);

        sku1.setProperties(properties);
        sku2.setProperties(properties);

        skus.add(sku1);
        skus.add(sku2);

        Product product = new Product();
        String title = "travelling";
        product.setSkus(skus);
        product.setTitle(title);
        product.setCategory(1);
        product.setUserId(1);
        product.setContent("gogogogogogo");

        productService.add(product);



    }

    @org.testng.annotations.Test
    public void testUpdate() throws Exception {

    }

    @org.testng.annotations.Test
    public void testGet() throws Exception {

    }
}