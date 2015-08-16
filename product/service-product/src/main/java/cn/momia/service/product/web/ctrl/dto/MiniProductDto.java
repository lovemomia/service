package cn.momia.service.product.web.ctrl.dto;

import cn.momia.service.product.facade.Product;

public class MiniProductDto implements ProductDto {
    protected Product product;

    public long getId() {
        return product.getId();
    }

    public String getThumb() {
        return product.getThumb();
    }

    public String getTitle() {
        return product.getTitle();
    }

    public String getAbstracts() {
        return product.getAbstracts();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MiniProductDto)) return false;

        MiniProductDto that = (MiniProductDto) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public MiniProductDto(Product product) {
        this.product = product;
    }
}
