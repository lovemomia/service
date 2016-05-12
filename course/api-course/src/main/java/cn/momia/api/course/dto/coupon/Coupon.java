package cn.momia.api.course.dto.coupon;

import java.math.BigDecimal;
import java.util.Date;

public class Coupon {
    public static class Src {
        public static final int INVITE = 1;
        public static final int FIRST_PAY = 2;
        public static final int ACTIVITY = 3;
        public static final int ACTIVITY_MULTI = 4;
    }

    public static final Coupon NOT_EXISTS_COUPON = new Coupon();

    private int id;
    private int src;
    private int count;
    private BigDecimal discount;
    private int timeType;
    private int time;
    private int timeUnit;
    private Date startTime;
    private Date endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getTimeType() {
        return timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
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

    public boolean exists() {
        return id > 0;
    }
}
