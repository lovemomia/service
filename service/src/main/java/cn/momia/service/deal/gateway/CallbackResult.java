package cn.momia.service.deal.gateway;

public class CallbackResult extends MapWrapper {
    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
