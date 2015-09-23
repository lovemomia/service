package cn.momia.service.comment;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class Comment {
    public static Comment NOT_EXIST_COMMENT = new Comment();

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

    public boolean exists() {
        return id > 0;
    }

    public boolean isInvalid() {
        return (orderId <= 0 ||
                productId <= 0 ||
                skuId <= 0 ||
                userId <= 0 ||
                star < 0 ||
                StringUtils.isBlank(content));
    }
}
