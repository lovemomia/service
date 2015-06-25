package cn.momia.service.deal.payment;

import java.io.Serializable;
import java.util.Date;

public class Payment implements Serializable {
    public static class Type {
        public static final int ALIPAY = 0;
        public static final int WECHATPAY = 1;
    }

    public static final Payment NOT_EXIST_PAYMENT = new Payment();
    static {
        NOT_EXIST_PAYMENT.setId(0);
    }

    private long id;
    private long orderId;
    private String payerId;
    private Date finishTime;
    private int payType;
    private String tradeNo;
    private float fee;

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

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;

        Payment payment = (Payment) o;

        return getId() == payment.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_PAYMENT);
    }
}
