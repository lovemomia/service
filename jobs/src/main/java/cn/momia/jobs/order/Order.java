package cn.momia.jobs.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Order implements Serializable {
    public static class Status {
        public static final int ALL = -1;
        public static final int DELETED = 0;
        public static final int NOT_PAYED = 1; // 已下单未付款
        public static final int PRE_PAYED = 2; // 准备付款
        public static final int PAYED = 3;     // 已付款
        public static final int FINISHED = 4;  // 已完成
        public static final int TO_REFUND = 5; // 申请退款
        public static final int REFUNDED = 6;  // 已退款
    }

    public static final Order NOT_EXIST_ORDER = new Order();
    static {
        NOT_EXIST_ORDER.setId(0);
    }

    private long id;
    private long customerId;
    private long productId;
    private long skuId;
    private List<OrderPrice> prices;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public List<OrderPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<OrderPrice> prices) {
        this.prices = prices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        return getId() == order.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_ORDER);
    }

    public BigDecimal getTotalFee() {
        BigDecimal totalFee = new BigDecimal(0);
        for (OrderPrice price : prices) {
            BigDecimal priceValue = price.getPrice().multiply(new BigDecimal(price.getCount()));
            totalFee = totalFee.add(priceValue);
        }

        return totalFee;
    }

    public int getCount() {
        int count = 0;
        for (OrderPrice price : prices) {
            count += price.getCount();
        }

        return count;
    }
}
