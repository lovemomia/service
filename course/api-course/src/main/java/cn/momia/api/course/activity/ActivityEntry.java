package cn.momia.api.course.activity;

public class ActivityEntry {
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

    public static final ActivityEntry NOT_EXIST_ACTIVITY_ENTRY = new ActivityEntry();

    private long id;
    private int activityId;
    private String mobile;
    private String childName;
    private String relationShip;
    private String extraMessage;
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public String getExtraMessage() {
        return extraMessage;
    }

    public void setExtraMessage(String extraMessage) {
        this.extraMessage = extraMessage;
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
}
