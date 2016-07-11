package cn.momia.api.course.expert;

import java.math.BigDecimal;

/**
 * Created by hoze on 16/6/21.
 */
public class ExpertOrder {

    public static class Status {
        public static final int DELETED = 0;
        public static final int NOT_PAYED = 1; // 已下单未付款
        public static final int PRE_PAYED = 2; // 准备付款
        public static final int PAYED = 3;     // 已付款
        public static final int FINISHED = 4;  // 已完成
        public static final int TO_REFUND = 5; // 申请退款
        public static final int REFUNDED = 6;  // 已退款
        public static final int REFUND_CHECKED = 7;  // 退款申请已通过
    }

    private int id;
    private long userId;
    private long courseId;
    private long questionId;
    private int hearId;
    private int type;
    private BigDecimal price;
    private String mobile;
    private int status;
    private boolean isUse = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public int getHearId() {
        return hearId;
    }

    public void setHearId(int hearId) {
        this.hearId = hearId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean exists() {
        return id > 0;
    }

    public boolean isPayed() {
        return status >= Status.PAYED;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setIsUse(boolean isUse) {
        this.isUse = isUse;
    }
}
