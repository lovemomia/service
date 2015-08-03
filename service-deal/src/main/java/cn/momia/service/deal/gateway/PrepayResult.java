package cn.momia.service.deal.gateway;

public abstract class PrepayResult extends MapWrapper {
    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
