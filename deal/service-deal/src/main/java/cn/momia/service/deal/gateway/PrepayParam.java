package cn.momia.service.deal.gateway;

public abstract class PrepayParam extends MapWrapper {
    private int clientType;

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public abstract long getOrderId();
}
