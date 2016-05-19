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
    private long userId;
    private long orderId;
    private long skuId;
    private BigDecimal price;
    private int courseCount;
    private int bookableCount;
    private int time;
    private int timeUnit;

    private long courseId;
    private String cover;
    private String title;

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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean exists() {
        return id > 0;
    }
}
