package cn.momia.service.course.coupon;

import java.math.BigDecimal;

public class CouponCode {
    public static final CouponCode NOT_EXIST_COUPON_CODE = new CouponCode();

    private int id;
    private String code;
    private BigDecimal discount;
    private BigDecimal consumption;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public boolean exists() {
        return id > 0;
    }
}
