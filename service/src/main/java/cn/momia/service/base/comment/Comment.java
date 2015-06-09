package cn.momia.service.base.comment;

import java.util.Date;

public class Comment {
    public static final Comment NOT_EXIST_COMMENT = new Comment();
    public static final Comment DUPLICATE_COMMENT = new Comment() {
        public boolean isDuplicated() {
            return true;
        }
    };

    static {
        NOT_EXIST_COMMENT.setId(0);
        DUPLICATE_COMMENT.setId(0);
    }

    public static class Type {
        public static final int SKU = 0;
        public static final int SERVER = 1;
    }

    public static class Star {
        public static final int ALL = 0;
        public static final int ONE = 1;
        public static final int TWO = 2;
        public static final int THREE = 3;
        public static final int FOUR = 4;
        public static final int FIVE = 5;
    }

    private long id;
    private long customerId;
    private long serverId;
    private long skuId;
    private int star;
    private String content;
    private Date addTime;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public boolean isDuplicated() {
        return false;
    }
}
