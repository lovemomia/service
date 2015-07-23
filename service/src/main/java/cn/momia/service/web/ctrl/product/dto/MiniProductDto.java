package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.Product;
import cn.momia.service.web.ctrl.dto.Dto;

public class MiniProductDto implements Dto {
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
