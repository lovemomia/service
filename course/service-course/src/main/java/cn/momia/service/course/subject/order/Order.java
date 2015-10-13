package cn.momia.service.course.subject.order;

import java.math.BigDecimal;

public class Order {
    public static final Order NOT_EXIST_ORDER = new Order();

    private long id;
    private long userId;
    private long subjectId;
    private long skuId;
    private BigDecimal price;
    private int count;
    private OrderContact contact;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
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

    public OrderContact getContact() {
        return contact;
    }

    public void setContact(OrderContact contact) {
        this.contact = contact;
    }

    public BigDecimal getTotalFee() {
        return price.multiply(new BigDecimal(count));
    }

    public boolean isInvalid() {
        return userId <= 0 || subjectId <= 0 || skuId <= 0 || price == null || count <= 0;
    }

    public boolean exists() {
        return id > 0;
    }
}
