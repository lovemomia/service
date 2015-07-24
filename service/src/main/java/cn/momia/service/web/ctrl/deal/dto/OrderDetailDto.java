package cn.momia.service.web.ctrl.deal.dto;

import cn.momia.service.common.MetaUtil;
import cn.momia.service.deal.order.Order;
import cn.momia.service.product.Product;
import cn.momia.service.web.ctrl.dto.Dto;

import java.math.BigDecimal;

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
        return MetaUtil.getRegionName(product.getRegionId());
    }

    public String getAddress() {
        return product.getAddress();
    }

    public BigDecimal getPrice() {
        return product.getMinPrice();
    }

    public OrderDetailDto(Order order, Product product) {
        super(order);
        this.product = product;
    }
}
