package cn.momia.mapi.api.v1.dto;

import cn.momia.mapi.api.v1.dto.base.BannerDto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;

import java.util.List;

public class HomeDto implements Dto {
    private List<BannerDto> banners;
    private List<ProductDto> products;
    private Integer nextpage = null;

    public List<BannerDto> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerDto> banners) {
        this.banners = banners;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public Integer getNextpage() {
        return nextpage;
    }

    public void setNextpage(Integer nextpage) {
        this.nextpage = nextpage;
    }
}
