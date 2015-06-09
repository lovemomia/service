package cn.momia.service.deal.order;

import java.util.Date;

public class OrderQuery {
    public static class Status {
        public static final int NOT_PAYED = 1;//已下单未付款
        public static final int PAYED = 2;//已付款
        public static final int FINISHED = 3;//已完成
        public static final int TO_REFUND = 4;//申请退款
        public static final int REFUNDED = 5;//
    }

    private long customerId;
    private long serverId;
    private int status;
    private Date startTime;
    private Date endTime;

    // 分页参数
    private int start;
    private int count;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
