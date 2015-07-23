package cn.momia.service.web.ctrl.deal.dto;

import cn.momia.service.deal.order.Order;
import cn.momia.service.product.Product;

import java.math.BigDecimal;

public class OrderDetailDto extends OrderDto {
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
        // TODO
        return null;
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
