package cn.momia.service.deal.web.ctrl.dto;

import cn.momia.api.base.MetaUtil;
import cn.momia.api.product.sku.Sku;
import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.deal.order.Order;
import cn.momia.api.product.Product;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetailDto extends OrderDto implements Dto {
    private Product product;

    public String getCover() {
        return product.getCover();
    }

    public String getTitle() {
        return product.getTitle();
    }

    public String getScheduler() {
        return product.getScheduler();
    }

    public String getRegion() {
        return MetaUtil.getRegionName(product.getSkuRegionId(getSkuId()));
    }

    public String getAddress() {
        return product.getSkuAddress(getSkuId());
    }

    public BigDecimal getPrice() {
        return product.getPrice();
    }

    public String getTime() {
        return product.getSkuTime(getSkuId());
    }

    public boolean isFinished() {
        List<Sku> skus = product.getSkus();
        if (skus == null || skus.isEmpty()) return true;
        for (Sku sku : skus) {
            if (sku.getSkuId() == getSkuId()) return sku.isFinished();
        }

        return true;
    }

    public boolean isClosed() {
        List<Sku> skus = product.getSkus();
        if (skus == null || skus.isEmpty()) return true;
        for (Sku sku : skus) {
            if (sku.getSkuId() == getSkuId()) return sku.isClosed();
        }

        return true;
    }

    public OrderDetailDto(Order order, Product product) {
        super(order);
        this.product = product;
    }
}
