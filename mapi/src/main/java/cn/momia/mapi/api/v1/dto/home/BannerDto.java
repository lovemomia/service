package cn.momia.mapi.api.v1.dto.home;

import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONObject;

public class BannerDto implements Dto {
    private String cover;
    private String action;

    public String getCover() {
        return cover;
    }

    public String getAction() {
        return action;
    }

    public BannerDto(JSONObject bannerJson) {
        this.cover = bannerJson.getString("cover");
        this.action = bannerJson.getString("action");
    }
}
