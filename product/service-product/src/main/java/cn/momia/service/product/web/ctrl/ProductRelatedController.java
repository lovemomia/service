package cn.momia.service.product.web.ctrl;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.product.dto.ProductDto;
import cn.momia.api.product.dto.SkuDto;
import cn.momia.api.product.dto.SkuPriceDto;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.facade.ProductImage;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuPrice;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class ProductRelatedController extends BaseController {
    @Autowired protected ProductServiceFacade productServiceFacade;

    private static final int STATUS_DELETED = 0;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_OFFLINE = 2;
    private static final int STATUS_END = 3;
    private static final int STATUS_SOLDOUT = 4;
    private static final int STATUS_FINISHED = 5;

    protected List<ProductDto> buildProductDtos(List<Product> products, int type, boolean withSku) {
        List<ProductDto> productDtos = new ArrayList<ProductDto>();
        for (Product product : products) {
            productDtos.add(buildProductDto(product, type, withSku));
        }

        return productDtos;
    }

    protected ProductDto buildProductDto(Product product, int type, boolean withSku) {
        ProductDto productDto = new ProductDto();

        switch (type) {
            case Product.Type.FULL:
                productDto.setImgs(getImgs(product));
                productDto.setContent(product.getContent());
            case Product.Type.BASE_WITH_SKU:
            case Product.Type.BASE:
                productDto.setCover(product.getCover());
                productDto.setJoined(product.getJoined());
                productDto.setScheduler(product.getScheduler());
                productDto.setTags(product.getTags());
                productDto.setRegionId(product.getRegionId());
                productDto.setRegion(MetaUtil.getRegionName(product.getRegionId()));
                productDto.setAddress(product.getAddress());
                productDto.setPoi(product.getPoi());
                productDto.setPrice(product.getMinPrice());
                productDto.setOriginalPrice(product.getMinOriginalPrice());
                productDto.setSoldOut(product.isSoldOut());
                productDto.setOpened(product.isOpened());
                productDto.setCrowd(product.getCrowd());
                productDto.setStock(product.isOpened() ? product.getStock() : 0);
                productDto.setStatus(getStatus(product));
            case Product.Type.MINI:
                productDto.setId(product.getId());
                productDto.setThumb(product.getThumb());
                productDto.setTitle(product.getTitle());
                productDto.setAbstracts(product.getAbstracts());
            default: break;
        }

        if (withSku) {
            List<SkuDto> skuDtos = new ArrayList<SkuDto>();
            for (Sku sku : product.getSkus()) {
                skuDtos.add(buildFullSkuDto(sku));
            }

            productDto.setSkus(skuDtos);
        }

        return productDto;
    }

    private int getStatus(Product product) {
        if (product.isFinished()) return STATUS_FINISHED;
        if (product.isEnd()) return STATUS_END;

        if (product.isOpened()) return STATUS_SOLDOUT;

        return product.getStatus();
    }

    private List<String> getImgs(Product product) {
        List<String> imgs = new ArrayList<String>();
        for (ProductImage productImage : product.getImgs()) imgs.add(productImage.getUrl());

        return imgs;
    }

    protected List<SkuDto> buildFullSkuDtos(List<Sku> skus) {
        List<SkuDto> skuDtos = new ArrayList<SkuDto>();
        for (Sku sku : skus) {
            skuDtos.add(buildFullSkuDto(sku));
        }

        return skuDtos;
    }

    protected SkuDto buildFullSkuDto(Sku sku) {
        SkuDto skuDto = buildBaseSkuDto(sku);
        skuDto.setNeedRealName(sku.isNeedRealName());
        skuDto.setPrices(buildSkuPriceDtos(sku.getPrices()));

        return skuDto;
    }

    protected SkuDto buildBaseSkuDto(Sku sku) {
        SkuDto skuDto = new SkuDto();
        skuDto.setProductId(sku.getProductId());
        skuDto.setSkuId(sku.getId());
        skuDto.setDesc(sku.getDesc());
        skuDto.setType(sku.getType());
        skuDto.setLimit(sku.getLimit() < 0 ? (sku.getType() == 1 ? 1 : 0) : sku.getLimit());
        skuDto.setStock(sku.getUnlockedStock() < 0 ? 0 : sku.getUnlockedStock());
        skuDto.setMinPrice(sku.getMinPrice());
        skuDto.setMinOriginalPrice(sku.getMinOriginalPrice());
        skuDto.setTime(sku.getFormatedTime());
        skuDto.setPlaceId(sku.getPlaceId());
        skuDto.setPlaceName(sku.getPlaceName());
        skuDto.setRegionId(sku.getRegionId());
        skuDto.setAddress(sku.getAddress());
        skuDto.setNeedLeader(sku.isNeedLeader());
        skuDto.setHasLeader(!sku.isNeedLeader() || sku.hasLeader());
        skuDto.setLeaderUserId(sku.getLeaderUserId());
        skuDto.setFull(sku.isFull());

        Date now = new Date();
        skuDto.setFinished(sku.isFinished(now));
        skuDto.setClosed(sku.isClosed(now));

        return skuDto;
    }

    private List<SkuPriceDto> buildSkuPriceDtos(List<SkuPrice> skuPrices) {
        List<SkuPriceDto> skuPriceDtos = new ArrayList<SkuPriceDto>();
        for (SkuPrice skuPrice : skuPrices) {
            skuPriceDtos.add(buildSkuPriceDto(skuPrice));
        }

        return skuPriceDtos;
    }

    private SkuPriceDto buildSkuPriceDto(SkuPrice skuPrice) {
        SkuPriceDto skuPriceDto = new SkuPriceDto();
        skuPriceDto.setAdult(skuPrice.getAdult());
        skuPriceDto.setChild(skuPrice.getChild());
        skuPriceDto.setPrice(skuPrice.getPrice());
        skuPriceDto.setOrigin(skuPrice.getOrigin());
        skuPriceDto.setUnit(skuPrice.getUnit());
        skuPriceDto.setDesc(skuPrice.getDesc());

        return skuPriceDto;
    }
}
