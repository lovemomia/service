package cn.momia.service.deal.gateway;

public class CallbackResult extends MapWrapper {
    private boolean successful;
    private long orderId;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
