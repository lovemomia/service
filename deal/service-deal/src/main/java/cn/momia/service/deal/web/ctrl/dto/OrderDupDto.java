package cn.momia.service.deal.web.ctrl.dto;

public class OrderDupDto {
    public static final OrderDupDto NOT_DUPLICATED = new OrderDupDto();
    static {
        NOT_DUPLICATED.setDuplicated(false);
    }

    private boolean duplicated;
    private Long orderId;
    private Boolean same;

    public boolean isDuplicated() {
        return duplicated;
    }

    public void setDuplicated(boolean duplicated) {
        this.duplicated = duplicated;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Boolean isSame() {
        return same;
    }

    public void setSame(Boolean same) {
        this.same = same;
    }
}
