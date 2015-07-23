package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.Product;

import java.math.BigDecimal;

public class BaseProductDto extends MiniProductDto {
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

    public BaseProductDto(Product product) {
        super(product);
    }
}
