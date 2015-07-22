package cn.momia.service.web.ctrl.deal.dto;

import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderPrice;
import cn.momia.service.product.Product;
import cn.momia.service.product.sku.Sku;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrderDto {
    private Order order;

    // product info
    private String cover;
    private String title;
    private String scheduler;
    private String address;
    private BigDecimal price;

    // sku info
    private String time;

    public long getId() {
        return order.getId();
    }

    public long getProductId() {
        return order.getProductId();
    }

    public long getSkuId() {
        return order.getSkuId();
    }

    public int getCount() {
        return order.getCount();
    }

    public BigDecimal getTotalFee() {
        return order.getTotalFee();
    }

    public String getParticipants() {
        return buildParticipants(order.getPrices());
    }

    private String buildParticipants(List<OrderPrice> prices) {
        int adult = 0;
        int child = 0;
        for (OrderPrice price : prices) {
            adult += price.getAdult();
            child += price.getChild();
        }

        if (adult > 0 && child > 0) return adult + "成人, " + child + "儿童";
        else if (adult <= 0 && child > 0) return child + "儿童";
        else if (adult > 0 && child <= 0) return adult + "成人";
        return "";
    }

    public String getContacts() {
        return order.getContacts();
    }

    public String getMobile() {
        return order.getMobile();
    }

    public Date getAddTime() {
        return order.getAddTime();
    }

    public int getStatus() {
        return order.getStatus();
    }

    public String getCover() {
        return cover;
    }

    public String getTitle() {
        return title;
    }

    public String getScheduler() {
        return scheduler;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public OrderDto(Order order) {
        this.order = order;
    }

    public OrderDto(Order order, Product product) {
        this(order);
        this.cover = product.getCover();
        this.title = product.getTitle();
        this.scheduler = product.getScheduler();
        this.address = product.getPlace().getAddress();
        this.price = product.getMinPrice();
    }

    public OrderDto(Order order, Product product, Sku sku) {
        this(order, product);
        this.time = sku.time();
    }
}
