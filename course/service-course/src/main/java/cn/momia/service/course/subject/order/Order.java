package cn.momia.service.course.subject.order;

import cn.momia.service.course.subject.SubjectSku;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private List<SubjectSku> skus;
    private Map<Long, Integer> counts;
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

    public List<SubjectSku> getSkus() {
        return skus;
    }

    public void setSkus(List<SubjectSku> skus) {
        this.skus = skus;
    }

    public Map<Long, Integer> getCounts() {
        return counts;
    }

    public void setCounts(Map<Long, Integer> counts) {
        this.counts = counts;
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
        int count = 0;
        for (int skuCount : counts.values()) {
            count += skuCount;
        }

        return count;
    }

    public int getJoinCount() {
        int joinCount = 0;
        for (SubjectSku sku : skus) {
            joinCount += sku.getJoinCount();
        }

        return joinCount;
    }

    public BigDecimal getTotalFee() {
        BigDecimal totalFee = new BigDecimal(0);
        for (SubjectSku sku : skus) {
            totalFee = totalFee.add(sku.getPrice().multiply(new BigDecimal(counts.get(sku.getId()))));
        }

        return totalFee;
    }

    public int getTotalCourseCount() {
        int totalCourseCount = 0;
        for (SubjectSku sku : skus) {
            totalCourseCount += sku.getCourseCount() * counts.get(sku.getId());
        }

        return totalCourseCount;
    }

    public boolean isInvalid() {
        return userId <= 0 || subjectId <= 0 || skus == null || skus.isEmpty();
    }

    public boolean isPayed() {
        return status >= Status.PAYED;
    }
}