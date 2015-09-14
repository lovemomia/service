package cn.momia.service.product.facade.impl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.service.DbAccessService;
import cn.momia.common.util.TimeUtil;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.facade.ProductImage;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.product.base.BaseProduct;
import cn.momia.service.product.base.BaseProductService;
import cn.momia.service.product.base.ProductSort;
import cn.momia.service.product.place.Place;
import cn.momia.service.product.place.PlaceService;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductServiceFacadeImpl extends DbAccessService implements ProductServiceFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceFacadeImpl.class);

    private BaseProductService baseProductService;
    private PlaceService placeService;
    private SkuService skuService;

    public void setBaseProductService(BaseProductService baseProductService) {
        this.baseProductService = baseProductService;
    }

    public void setPlaceService(PlaceService placeService) {
        this.placeService = placeService;
    }

    public void setSkuService(SkuService skuService) {
        this.skuService = skuService;
    }

    @Override
    public Product get(long productId) {
        return get(productId, false);
    }

    @Override
    public Product get(long productId, boolean mini) {
        if (productId <= 0) return Product.NOT_EXIST_PRODUCT;

        BaseProduct baseProduct = baseProductService.get(productId);
        if (!baseProduct.exists()) return Product.NOT_EXIST_PRODUCT;

        Product product = new Product();
        product.setBaseProduct(baseProduct);

        if (!mini) {
            product.setImgs(getProductImgs(baseProduct.getId()));

            List<Place> places = placeService.get(baseProduct.getPlaces());
            if (places.isEmpty()) return Product.NOT_EXIST_PRODUCT;

            product.setPlaces(places);
            product.setSkus(buildFullSkus(skuService.queryByProduct(baseProduct.getId())));
        }

        return product;
    }

    private List<Sku> buildFullSkus(List<Sku> skus) {
        List<Integer> placeIds = new ArrayList<Integer>();
        for (Sku sku : skus) placeIds.add(sku.getPlaceId());
        List<Place> places = placeService.get(placeIds);
        Map<Integer, Place> placesMap = new HashMap<Integer, Place>();
        for (Place place : places) placesMap.put(place.getId(), place);

        for (Sku sku : skus) sku.setPlace(placesMap.get(sku.getPlaceId()));

        return skus;
    }

    private List<ProductImage> getProductImgs(long productId) {
        final List<ProductImage> imgs = new ArrayList<ProductImage>();
        String sql = "SELECT url, width, height FROM t_product_img WHERE productId=? AND status=1";
        jdbcTemplate.query(sql, new Object[] { productId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                imgs.add(buildImage(rs));
            }
        });

        return imgs;
    }

    private ProductImage buildImage(ResultSet rs) throws SQLException {
        ProductImage img = new ProductImage();
        img.setUrl(rs.getString("url"));
        img.setWidth(rs.getInt("width"));
        img.setHeight(rs.getInt("height"));

        return img;
    }

    @Override
    public List<Product> list(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return new ArrayList<Product>();

        List<BaseProduct> baseProducts = baseProductService.get(productIds);
        return buildProducts(baseProducts);
    }

    private List<Product> buildProducts(List<BaseProduct> baseProducts) {
        List<Product> products = new ArrayList<Product>();
        if (baseProducts.isEmpty()) return products;

        List<Long> productIds = new ArrayList<Long>();
        List<Integer> placeIds = new ArrayList<Integer>();
        for (BaseProduct baseProduct : baseProducts) {
            if (!baseProduct.exists()) continue;
            productIds.add(baseProduct.getId());
            placeIds.addAll(baseProduct.getPlaces());
        }

        Map<Long, List<ProductImage>> imgsOfProducts = getProductsImgs(productIds);
        Map<Integer, Place> placesOfProducts = new HashMap<Integer, Place>();
        for (Place place : placeService.get(placeIds)) placesOfProducts.put(place.getId(), place);
        List<Sku> skus = buildFullSkus(skuService.queryByProducts(productIds));
        Map<Long, List<Sku>> skusOfProducts = new HashMap<Long, List<Sku>>();
        for (Sku sku : skus) {
            List<Sku> skusOfProduct = skusOfProducts.get(sku.getProductId());
            if (skusOfProduct == null) {
                skusOfProduct = new ArrayList<Sku>();
                skusOfProducts.put(sku.getProductId(), skusOfProduct);
            }
            skusOfProduct.add(sku);
        }

        for (BaseProduct baseProduct : baseProducts) {
            if (!baseProduct.exists()) continue;
            Product product = new Product();
            product.setBaseProduct(baseProduct);
            product.setImgs(imgsOfProducts.get(baseProduct.getId()));
            List<Place> places = new ArrayList<Place>();
            for (int placeId : baseProduct.getPlaces()) {
                Place place = placesOfProducts.get(placeId);
                if (place != null) places.add(place);
            }
            product.setPlaces(places);
            product.setSkus(skusOfProducts.get(baseProduct.getId()));

            if (!product.isInvalid()) products.add(product);
        }

        return products;
    }

    private Map<Long, List<ProductImage>> getProductsImgs(List<Long> productIds) {
        final Map<Long, List<ProductImage>> imgsOfProducts = new HashMap<Long, List<ProductImage>>();
        if (productIds.isEmpty()) return imgsOfProducts;

        try {
            String sql = "SELECT productId, url, width, height FROM t_product_img WHERE productId IN (" + StringUtils.join(productIds, ",") + ") AND status=1";
            jdbcTemplate.query(sql, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    long productId = rs.getLong("productId");
                    ProductImage img = buildImage(rs);
                    List<ProductImage> imgs = imgsOfProducts.get(productId);
                    if (imgs == null) {
                        imgs = new ArrayList<ProductImage>();
                        imgsOfProducts.put(productId, imgs);
                    }
                    imgs.add(img);
                }
            });
        } catch (Exception e) {
            LOGGER.error("fail to get imgs of products: {}", productIds, e);
        }

        return imgsOfProducts;
    }

    @Override
    public Map<Long, List<Product>> listGrouped(Map<Long, List<Long>> groupedProductIds) {
        Set<Long> productIds = new HashSet<Long>();
        for (List<Long> ids : groupedProductIds.values()) productIds.addAll(ids);
        List<Product> products = list(productIds);
        Map<Long, Product> productsMap = new HashMap<Long, Product>();
        for (Product product : products) productsMap.put(product.getId(), product);

        Map<Long, List<Product>> groupedProducts = new HashMap<Long, List<Product>>();
        for (Map.Entry<Long, List<Long>> entry : groupedProductIds.entrySet()) {
            long groupId = entry.getKey();
            List<Product> productsOfGroup = groupedProducts.get(groupId);
            if (productsOfGroup == null) {
                productsOfGroup = new ArrayList<Product>();
                groupedProducts.put(groupId, productsOfGroup);
            }

            for (long productId : entry.getValue()) {
                Product product = productsMap.get(productId);
                if (product != null) productsOfGroup.add(product);
            }
        }

        return groupedProducts;
    }

    @Override
    public String getDetail(long productId) {
        if (productId <= 0) return "";
        return baseProductService.getDetail(productId);
    }

    @Override
    public long queryCount(int cityId) {
        if (cityId < 0) return 0;
        return baseProductService.queryCount(cityId);
    }

    @Override
    public List<Product> query(int cityId, int start, int count, ProductSort productSort) {
        return buildProducts(baseProductService.query(cityId, start, count, productSort));
    }

    @Override
    public long queryCountByWeekend(int cityId) {
        if (cityId < 0) return 0;
        return baseProductService.queryCountByWeekend(cityId);
    }

    @Override
    public List<Product> queryByWeekend(int cityId, int start, int count) {
        return buildProducts(baseProductService.queryByWeekend(cityId, start, count));
    }

    @Override
    public List<Product> queryByMonth(int cityId, int month) {
        if (cityId < 0 || month <= 0 || month > 12) return new ArrayList<Product>();
        return buildProducts(baseProductService.queryByMonth(cityId, TimeUtil.formatYearMonth(month), TimeUtil.formatNextYearMonth(month)));
    }

    @Override
    public long queryCountNeedLeader(int cityId) {
        if (cityId < 0) return 0;
        return baseProductService.queryCountNeedLeader(cityId);
    }

    @Override
    public List<Product> queryNeedLeader(int cityId, int start, int count) {
        return buildProducts(baseProductService.queryNeedLeader(cityId, start, count));
    }

    @Override
    public boolean sold(long productId, int count) {
        if (productId <= 0 || count <= 0) return true;
        return baseProductService.sold(productId, count);
    }

    @Override
    public List<Sku> listSkus(long productId) {
        if (productId <= 0) return new ArrayList<Sku>();
        return buildFullSkus(skuService.queryByProduct(productId));
    }

    @Override
    public Sku getSku(long skuId) {
        if (skuId <= 0) return Sku.NOT_EXIST_SKU;
        return buildFullSku(skuService.get(skuId));
    }

    private Sku buildFullSku(Sku sku) {
        int placeId = sku.getPlaceId();
        if (placeId > 0) {
            Place place = placeService.get(placeId);
            if (place.exists()) sku.setPlace(place);
        }

        return sku;
    }

    @Override
    public boolean lockStock(long productId, long skuId, int count, int joined) {
        if (productId <= 0 || skuId <= 0 || count <= 0) return false;

        boolean successful = skuService.lock(skuId, count);
        if (successful) {
            try {
                if (isSoldOut(productId)) baseProductService.soldOut(productId);
                baseProductService.join(productId, joined);
            } catch (Exception e) {
                LOGGER.error("fail to update sold out/joined status of product: {}", productId, e);
            }
        }

        return successful;
    }

    // TODO 高并发下 soldOut状态的更新不稳定，容易出问题
    private boolean isSoldOut(long id) {
        if (id <= 0) return true;

        int unlockedStock = 0;
        List<Sku> skus = Sku.filterClosed(listSkus(id));
        for (Sku sku : skus) {
            if (sku.getType() == Sku.Type.NO_CEILING) return false;
            unlockedStock += sku.getUnlockedStock();
        }

        return unlockedStock <= 0;
    }

    @Override
    public boolean unlockStock(long productId, long skuId, int count, int joined) {
        if (productId <= 0 || skuId <= 0 || count <= 0) return true;

        boolean successful = skuService.unlock(skuId, count);
        if (successful) {
            try {
                if (!isSoldOut(productId)) baseProductService.unSoldOut(productId);
                baseProductService.decreaseJoined(productId, joined);
            } catch (Exception e) {
                LOGGER.error("fail to update sold out/joined status of product: {}", productId, e);
            }
        }

        return successful;
    }

    @Override
    public boolean addSkuLeader(long userId, long productId, long skuId) {
        if (userId <= 0 || productId <= 0 || skuId <= 0) return false;

        Sku sku = skuService.get(skuId);
        if (!sku.exists() || sku.isClosed(new Date()) || !sku.isNeedLeader()) throw new MomiaFailedException("活动已结束或不需要领队");

        return skuService.addLeader(userId, productId, skuId);
    }

    @Override
    public long queryCountOfLedSkus(long userId) {
        if (userId <= 0) return 0;
        return skuService.queryCountOfLedSkus(userId);
    }

    @Override
    public List<Sku> queryLedSkus(long userId, int start, int count) {
        if (userId <= 0 || start < 0 || count <= 0) return new ArrayList<Sku>();
        return buildFullSkus(skuService.queryLedSkus(userId, start, count));
    }
}
