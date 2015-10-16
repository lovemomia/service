package cn.momia.service.course.subject.order;

import cn.momia.common.service.Entity;

import java.math.BigDecimal;

public class OrderSku implements Entity {
    public static final OrderSku NOT_EXIST_ORDER_SKU = new OrderSku();

    private long id;
    private long orderId;
    private long skuId;
    private BigDecimal price;
    private int count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean exists() {
        return id > 0;
    }
}
