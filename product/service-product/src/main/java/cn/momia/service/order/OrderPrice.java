package cn.momia.service.order;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public class OrderPrice {
    private BigDecimal price;
    private int count;
    private int adult;
    private int child;

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

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderPrice)) return false;

        OrderPrice other = (OrderPrice) o;

        if (getCount() != other.getCount()) return false;
        if (getAdult() != other.getAdult()) return false;
        if (getChild() != other.getChild()) return false;
        return getPrice() == null ? other.getPrice() == null : getPrice().equals(other.getPrice());
    }

    @Override
    public int hashCode() {
        int result = getPrice() != null ? getPrice().hashCode() : 0;
        result = 31 * result + getCount();
        result = 31 * result + getAdult();
        result = 31 * result + getChild();

        return result;
    }

    public OrderPrice() {}

    public OrderPrice(JSONObject priceJson) {
        this.price = priceJson.getBigDecimal("price");
        this.count = priceJson.getInteger("count");
        this.adult = priceJson.containsKey("adult") ? priceJson.getInteger("adult") : 0;
        this.child = priceJson.containsKey("child") ? priceJson.getInteger("child") : 0;
    }
}
