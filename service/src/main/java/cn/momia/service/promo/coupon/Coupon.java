package cn.momia.service.promo.coupon;

import java.math.BigDecimal;
import java.util.Date;

public class Coupon {
    public static class Usage {
        public static final int NORMAL = 0;
        public static final int REGISTER = 1;
    }

    public static final Coupon NOT_EXIST_COUPON = new Coupon();
    public static final Coupon INVALID_COUPON = new Coupon();
    static {
        NOT_EXIST_COUPON.setId(0);
        INVALID_COUPON.setId(0);
    }

    private int id;
    private int type;
    private String title;
    private String desc;
    private BigDecimal discount;
    private BigDecimal consumption;
    private int accumulation;
    private Date startTime;
    private Date endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getConsumption() {
        return consumption;
    }

    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
    }

    public int getAccumulation() {
        return accumulation;
    }

    public void setAccumulation(int accumulation) {
        this.accumulation = accumulation;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coupon)) return false;

        Coupon coupon = (Coupon) o;

        return getId() == coupon.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_COUPON);
    }

    public boolean invalid() {
        return this == INVALID_COUPON;
    }
}
