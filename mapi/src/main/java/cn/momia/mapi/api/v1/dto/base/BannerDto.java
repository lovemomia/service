package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;

public class BannerDto implements Dto {
    private String cover;
    private String action;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BannerDto() {

    }

    public String getAction() {
        return action;
    }

    public BannerDto(JSONObject bannerJson) {
        this.cover = bannerJson.getString("cover");
        this.action = bannerJson.getString("action");
    }
}
