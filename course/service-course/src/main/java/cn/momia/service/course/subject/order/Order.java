package cn.momia.service.course.subject.order;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Order {
    public static class Status {
        public static final int DELETED = 0;
        public static final int NOT_PAYED = 1; // 已下单未付款
        public static final int PRE_PAYED = 2; // 准备付款
        public static final int PAYED = 3;     // 已付款
        public static final int FINISHED = 4;  // 已完成
        public static final int TO_REFUND = 5; // 申请退款
        public static final int REFUNDED = 6;  // 已退款
    }

    public static final Order NOT_EXIST_ORDER = new Order();

    private long id;
    private long userId;
    private long subjectId;
    private List<OrderPackage> packages;
    private String contact;
    private String mobile;

    private int status;
    private Date addTime;

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

    public List<OrderPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<OrderPackage> packages) {
        this.packages = packages;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public boolean exists() {
        return id > 0;
    }

    public int getCount() {
        return packages.size();
    }

    public BigDecimal getTotalFee() {
        BigDecimal totalFee = new BigDecimal(0);
        for (OrderPackage orderPackage : packages) {
            totalFee = totalFee.add(orderPackage.getPrice());
        }

        return totalFee;
    }

    public int getBookableCourseCount() {
        int bookableCourseCount = 0;
        for (OrderPackage orderPackage : packages) {
            bookableCourseCount += orderPackage.getBookableCount();
        }

        return bookableCourseCount;
    }

    public boolean isInvalid() {
        return userId <= 0 || subjectId <= 0 || packages == null || packages.isEmpty();
    }

    public boolean isPayed() {
        return status >= Status.PAYED;
    }
}
