package cn.momia.service.base.comment;

import com.alibaba.fastjson.JSONObject;

public class Comment {
    public static final Comment NOT_EXIST_COMMENT = new Comment();
    static {
        NOT_EXIST_COMMENT.setId(0);
    }

    private long id;
    private long customerId;
    private long serverId;
    private long productId;
    private long skuId;
    private int star;
    private String content;

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

    public Comment() {

    }

    public Comment(JSONObject jsonObject){
        if (jsonObject.containsKey("id")) setId(jsonObject.getInteger("id"));
        setCustomerId(jsonObject.getInteger("customerId"));
        setServerId(jsonObject.getInteger("serverId"));
        setProductId(jsonObject.getInteger("productId"));
        setSkuId(jsonObject.getInteger("skuId"));
        setStar(jsonObject.getInteger("star"));
        setContent(jsonObject.getString("content"));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_COMMENT);
    }
}
