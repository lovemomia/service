package cn.momia.product;

import com.alibaba.fastjson.JSONObject;

public class ProductImage {
    private String url;
    private int width;
    private int height;

    public ProductImage(JSONObject jsonObject) {
        setUrl(jsonObject.getString("url"));
        setWidth(jsonObject.getInteger("width"));
        setHeight(jsonObject.getInteger("height"));
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
