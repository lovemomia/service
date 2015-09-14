package cn.momia.service.product.web.ctrl.dto;

import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuPrice;

import java.util.List;

public class FullSkuDto extends BaseSkuDto {
    public boolean isNeedRealName() {
        return sku.isNeedRealName();
    }

    public List<SkuPrice> getPrices() {
        return sku.getPrices();
    }

    public FullSkuDto(Sku sku) {
        super(sku);
    }
}
