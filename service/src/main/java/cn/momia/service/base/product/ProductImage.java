package cn.momia.service.base.product;

import com.alibaba.fastjson.JSONObject;

public class ProductImage {

    public static final ProductImage NOT_EXIST_IMG = new ProductImage();
    static {
        NOT_EXIST_IMG.setId(0);
    }
    private long id;
    private long productId;
    private String url;
    private int width;
    private int height;

    public ProductImage(JSONObject jsonObject) {
        setUrl(jsonObject.getString("url"));
        setWidth(jsonObject.getInteger("width"));
        setHeight(jsonObject.getInteger("height"));
    }

    public ProductImage() {

    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
