package cn.momia.service.course.order;

import java.math.BigDecimal;

public class OrderPackage {
    public static class Type {
        public static final int PACKAGE = 1;
        public static final int SINGLE_COURSE = 2;
        public static final int TRIAL = 3;
    }

    public static final OrderPackage NOT_EXIST_ORDER_PACKAGE = new OrderPackage();

    private long id;
    private long orderId;
    private long skuId;
    private BigDecimal price;
    private int courseCount;
    private int bookableCount;

    private long courseId;

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

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

    public int getBookableCount() {
        return bookableCount;
    }

    public void setBookableCount(int bookableCount) {
        this.bookableCount = bookableCount;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public boolean exists() {
        return id > 0;
    }
}
