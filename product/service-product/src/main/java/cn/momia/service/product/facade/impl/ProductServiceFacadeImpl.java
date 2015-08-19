package cn.momia.service.product.facade.impl;

import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.base.util.TimeUtil;
import cn.momia.service.base.impl.DbAccessService;
import cn.momia.service.product.banner.Banner;
import cn.momia.service.product.banner.BannerService;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.facade.ProductImage;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.product.base.BaseProduct;
import cn.momia.service.product.base.BaseProductService;
import cn.momia.service.product.base.ProductSort;
import cn.momia.service.product.favorite.FavoriteService;
import cn.momia.service.product.place.Place;
import cn.momia.service.product.place.PlaceService;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuService;
import cn.momia.service.product.topic.Topic;
import cn.momia.service.product.topic.TopicGroup;
import cn.momia.service.product.topic.TopicService;
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

    private static final int MAX_BANNER_COUNT = 20;

    private BannerService bannerService;
    private BaseProductService baseProductService;
    private PlaceService placeService;
    private SkuService skuService;
    private TopicService topicService;

    private FavoriteService favoriteService;

    public void setBannerService(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    public void setBaseProductService(BaseProductService baseProductService) {
        this.baseProductService = baseProductService;
    }

    public void setPlaceService(PlaceService placeService) {
        this.placeService = placeService;
    }

    public void setSkuService(SkuService skuService) {
        this.skuService = skuService;
    }

    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }

    public void setFavoriteService(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Override
    public List<Banner> getBanners(int cityId, int count) {
        if (cityId < 0 || count <= 0 || count > MAX_BANNER_COUNT) return new ArrayList<Banner>();
        return bannerService.getBanners(cityId, count);
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

            Place place = placeService.get(baseProduct.getPlaceId());
            if (!place.exists()) return Product.NOT_EXIST_PRODUCT;

            product.setPlace(place);
            product.setSkus(skuService.queryByProduct(baseProduct.getId()));
        }

        return product;
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
    public List<Product> get(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return new ArrayList<Product>();

        List<BaseProduct> baseProducts = baseProductService.get(productIds);
        return buildProducts(baseProducts);
    }

    private List<Product> buildProducts(List<BaseProduct> baseProducts) {
        List<Product> products = new ArrayList<Product>();
        if (baseProducts.isEmpty()) return products;

        List<Long> productIds = new ArrayList<Long>();
        List<Long> placeIds = new ArrayList<Long>();
        for (BaseProduct baseProduct : baseProducts) {
            if (!baseProduct.exists()) continue;
            productIds.add(baseProduct.getId());
            placeIds.add(baseProduct.getPlaceId());
        }

        Map<Long, List<ProductImage>> imgsOfProducts = getProductsImgs(productIds);
        Map<Long, Place> placeOfProducts = placeService.get(placeIds);
        Map<Long, List<Sku>> skusOfProducts = skuService.queryByProducts(productIds);

        for (BaseProduct baseProduct : baseProducts) {
            if (!baseProduct.exists()) continue;
            Product product = new Product();
            product.setBaseProduct(baseProduct);
            product.setImgs(imgsOfProducts.get(baseProduct.getId()));
            product.setPlace(placeOfProducts.get(baseProduct.getPlaceId()));
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
    public long queryCountByMonth(int cityId, int month) {
        if (cityId < 0 || month <= 0 || month > 12) return 0;
        return baseProductService.queryCountByMonth(cityId, TimeUtil.formatMonth(month), TimeUtil.formatNextMonth(month));
    }

    @Override
    public List<Product> queryByMonth(int cityId, int month) {
        if (cityId < 0 || month <= 0 || month > 12) return new ArrayList<Product>();
        return buildProducts(baseProductService.queryByMonth(cityId, TimeUtil.formatMonth(month), TimeUtil.formatNextMonth(month)));
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
    public long queryCountOfLedSkus(long userId) {
        if (userId <= 0) return 0;
        return skuService.queryCountOfLedSkus(userId);
    }

    @Override
    public List<Sku> queryLedSkus(long userId, int start, int count) {
        if (userId <= 0 || start < 0 || count <= 0) return new ArrayList<Sku>();
        return skuService.queryLedSkus(userId, start, count);
    }

    @Override
    public List<Sku> getSkus(long productId) {
        if (productId <= 0) return new ArrayList<Sku>();
        return skuService.queryByProduct(productId);
    }

    @Override
    public List<Sku> getAllSkus(long productId) {
        if (productId <= 0) return new ArrayList<Sku>();
        return skuService.queryAllByProduct(productId);
    }

    @Override
    public Sku getSku(long skuId) {
        if (skuId <= 0) return Sku.NOT_EXIST_SKU;
        return skuService.get(skuId);
    }

    @Override
    public List<Sku> getSkusWithoutLeader(long productId) {
        if (productId <= 0) return new ArrayList<Sku>();
        List<Sku> skus = Sku.filterClosed(skuService.queryByProduct(productId));
        List<Sku> skusWithoutLeader = new ArrayList<Sku>();
        for (Sku sku : skus) {
            if (!sku.hasLeader()) skusWithoutLeader.add(sku);
        }

        return skusWithoutLeader;
    }

    @Override
    public boolean addSkuLeader(long userId, long productId, long skuId) {
        if (userId <= 0 || productId <= 0 || skuId <= 0) return false;

        Sku sku = skuService.get(skuId);
        if (!sku.exists() || sku.isClosed(new Date()) || !sku.isNeedLeader()) throw new MomiaFailedException("活动已结束或不需要领队");

        return skuService.addLeader(userId, productId, skuId);
    }

    @Override
    public boolean lockStock(long productId, long skuId, int count, int joined) {
        if (productId <= 0 || skuId <= 0 || count <= 0) return false;

        boolean successful = skuService.lock(skuId, count);
        try {
            if (successful) {
                if (isSoldOut(productId)) baseProductService.soldOut(productId);
                baseProductService.join(productId, joined);
            }
        } catch (Exception e) {
            LOGGER.error("fail to update sold out/joined status of product: {}", productId, e);
        }

        return successful;
    }

    private boolean isSoldOut(long id) {
        if (id <= 0) return true;

        int unlockedStock = 0;
        List<Sku> skus = getSkus(id);
        for (Sku sku : skus) {
            if (sku.getType() == Sku.Type.NO_CEILING) return false;
            unlockedStock += sku.getUnlockedStock();
        }

        return unlockedStock <= 0;
    }

    @Override
    public boolean unlockStock(long productId, long skuId, int count, int joined) {
        if (productId <= 0 || skuId <= 0 || count <= 0) return true;

        try {
            baseProductService.unSoldOut(productId);
        } catch (Exception e) {
            LOGGER.error("fail to set sold out status of product: {}", productId, e);
        }

        boolean successful = skuService.unlock(skuId, count);
        if (successful) {
            try {
                baseProductService.decreaseJoined(productId, joined);
            } catch (Exception e) {
                LOGGER.error("fail to decrease joined of product: {}", productId, e);
            }
        }

        return successful;
    }

    @Override
    public boolean sold(long productId, int count) {
        if (productId <= 0 || count <= 0) return true;
        return baseProductService.sold(productId, count);
    }

    @Override
    public boolean isFavoried(long userId, long productId) {
        if (userId <= 0 || productId <= 0) return false;
        return favoriteService.isFavoried(userId, productId);
    }

    @Override
    public boolean favor(long userId, long productId) {
        if (userId <= 0 || productId <= 0) return false;
        if (isFavoried(userId, productId)) return true;
        return favoriteService.favor(userId, productId);
    }

    @Override
    public boolean unFavor(long userId, long productId) {
        if (userId <= 0 || productId <= 0) return true;
        if (!isFavoried(userId, productId)) return true;
        return favoriteService.unFavor(userId, productId);
    }

    @Override
    public long queryFavoritesCount(long userId) {
        if (userId <= 0) return 0;
        return favoriteService.queryCount(userId);
    }

    @Override
    public List<Product> queryFavorites(long userId, int start, int count) {
        List<Long> productIds = favoriteService.query(userId, start, count);

        return get(productIds);
    }

    @Override
    public Topic getTopic(long topicId) {
        return topicService.get(topicId);
    }

    @Override
    public List<TopicGroup> getTopicGroups(long topicId) {
        if (topicId <= 0) return new ArrayList<TopicGroup>();
        return topicService.getTopicGroups(topicId);
    }

    @Override
    public Map<Long, List<Product>> queryByTopicGroups(List<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) return new HashMap<Long, List<Product>>();

        Map<Long, List<Long>> groupedProductIds = topicService.getProductIds(groupIds);

        Set<Long> productIds = new HashSet<Long>();
        for (List<Long> ids : groupedProductIds.values()) productIds.addAll(ids);
        List<Product> products = get(productIds);
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
}
