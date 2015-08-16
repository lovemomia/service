package cn.momia.service.deal.gateway;

public class PrepayResult extends MapWrapper {
    public static final PrepayResult FAILED = new PrepayResult();
    static {
        FAILED.setSuccessful(false);
    }

    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
