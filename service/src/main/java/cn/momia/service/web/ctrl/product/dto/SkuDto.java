package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuPrice;
import cn.momia.service.web.ctrl.dto.Dto;
import cn.momia.service.web.ctrl.dto.ListDto;

import java.math.BigDecimal;
import java.util.List;

public class SkuDto implements Dto {
    public static ListDto toSkusDto(List<Sku> skus) {
        ListDto skusDto = new ListDto();

        skus = Sku.filter(skus);
        for (Sku sku : skus) {
            skusDto.add(new SkuDto(sku));
        }

        return skusDto;
    }

    private Sku sku;

    public long getProductId() {
        return sku.getProductId();
    }

    public long getSkuId() {
        return sku.getId();
    }

    public String getDesc() {
        return sku.getDesc();
    }

    public int getType() {
        return sku.getType();
    }

    public int getLimit() {
        int limit = sku.getLimit();
        return limit < 0 ? (getType() == 1 ? 1 : 0) : limit;
    }

    public boolean isNeedRealName() {
        return sku.isNeedRealName();
    }

    public int getStock() {
        int stock = sku.getUnlockedStock();
        return stock < 0 ? 0 : stock;
    }

    public BigDecimal getMinPrice() {
        return sku.getMinPrice();
    }

    public String getTime() {
        return sku.getTime();
    }

    public List<SkuPrice> getPrices() {
        return sku.getPrices();
    }

    public SkuDto(Sku sku) {
        this.sku = sku;
    }
}
