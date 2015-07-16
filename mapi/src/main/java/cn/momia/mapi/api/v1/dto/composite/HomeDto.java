package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;

public class HomeDto implements Dto {
    public static final HomeDto EMPTY = new HomeDto();
    static {
        EMPTY.setBanners(new ListDto());
        EMPTY.setProducts(new ListDto());
    }

    private ListDto banners;
    private ListDto products;
    private Integer nextpage = null;

    public ListDto getBanners() {
        return banners;
    }

    public void setBanners(ListDto banners) {
        this.banners = banners;
    }

    public ListDto getProducts() {
        return products;
    }

    public void setProducts(ListDto products) {
        this.products = products;
    }

    public Integer getNextpage() {
        return nextpage;
    }

    public void setNextpage(Integer nextpage) {
        this.nextpage = nextpage;
    }
}
