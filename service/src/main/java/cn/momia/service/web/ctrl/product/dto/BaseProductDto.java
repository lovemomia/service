package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.Product;
import cn.momia.service.web.ctrl.dto.ListDto;

import java.math.BigDecimal;

public class BaseProductDto extends MiniProductDto {
    private boolean withSku = false;

    public String getCover() {
        return product.getCover();
    }

    public int getJoined() {
        return product.getJoined();
    }

    public String getScheduler() {
        return product.getScheduler();
    }

    public String getRegion() {
        // TODO
        return null;
    }

    public String getAddress() {
        return product.getAddress();
    }

    public String getPoi() {
        return product.getPoi();
    }

    public BigDecimal getPrice() {
        return product.getMinPrice();
    }

    public boolean isSoldOut() {
        return product.isSoldOut();
    }

    public boolean isOpened() {
        return product.isOpened();
    }

    public ListDto getSkus() {
        return withSku ? SkuDto.toSkusDto(product.getSkus()) : null;
    }

    public BaseProductDto(Product product) {
        super(product);
    }

    public BaseProductDto(Product product, boolean withSku) {
        this(product);
        this.withSku = withSku;
    }
}
