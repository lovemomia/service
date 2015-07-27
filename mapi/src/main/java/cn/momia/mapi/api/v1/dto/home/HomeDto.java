package cn.momia.mapi.api.v1.dto.home;

import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONArray;

public class HomeDto implements Dto {
    public static final HomeDto EMPTY = new HomeDto();
    static {
        EMPTY.setProducts(new JSONArray());
    }

    private JSONArray banners;
    private JSONArray products;
    private Integer nextpage = null;

    public JSONArray getBanners() {
        return banners;
    }

    public void setBanners(JSONArray banners) {
        this.banners = banners;
    }

    public JSONArray getProducts() {
        return products;
    }

    public void setProducts(JSONArray products) {
        this.products = products;
    }

    public Integer getNextpage() {
        return nextpage;
    }

    public void setNextpage(Integer nextpage) {
        this.nextpage = nextpage;
    }
}
