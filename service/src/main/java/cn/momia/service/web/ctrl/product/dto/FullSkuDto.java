package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuPrice;
import cn.momia.service.user.leader.Leader;

import java.util.List;

public class FullSkuDto extends BaseSkuDto {
    public List<SkuPrice> getPrices() {
        return sku.getPrices();
    }

    public FullSkuDto(Sku sku) {
        super(sku);
    }

    public FullSkuDto(Sku sku, Leader leader) {
        super(sku, leader);
    }
}
