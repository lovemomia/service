package cn.momia.service.product.comment;

import java.util.Date;
import java.util.List;

public class Comment {
    public static Comment NOT_EXIST_COMMENT = new Comment();
    public static Comment INVALID_COMMENT = new Comment();
    static {
        NOT_EXIST_COMMENT.setId(0);
        INVALID_COMMENT.setId(0);
    }

    private long id;
    private long orderId;
    private long productId;
    private long skuId;
    private long userId;
    private int star;
    private String content;
    private Date addTime;
    private List<CommentImage> imgs;

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

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public List<CommentImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<CommentImage> imgs) {
        this.imgs = imgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;

        Comment comment = (Comment) o;

        return getId() == comment.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_COMMENT);
    }
}
