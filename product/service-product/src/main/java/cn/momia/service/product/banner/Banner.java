package cn.momia.service.product.banner;

import java.io.Serializable;

public class Banner implements Serializable {
    private String cover;
    private String action;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
