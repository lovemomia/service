package cn.momia.api.product.dto;

public class OrderDupDto {
    public static final OrderDupDto NOT_DUPLICATED = new OrderDupDto();
    static {
        NOT_DUPLICATED.setDuplicated(false);
    }

    private boolean duplicated;
    private Long orderId;
    private Long productId;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Boolean isSame() {
        return same;
    }

    public void setSame(Boolean same) {
        this.same = same;
    }
}
