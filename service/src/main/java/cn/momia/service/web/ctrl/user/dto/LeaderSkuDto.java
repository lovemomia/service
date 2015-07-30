package cn.momia.service.web.ctrl.user.dto;

import cn.momia.service.product.sku.Sku;
import cn.momia.service.web.ctrl.dto.Dto;
import cn.momia.service.web.ctrl.dto.ListDto;

import java.util.List;

public class LeaderSkuDto implements Dto {
    public static ListDto toLeaderSkusDto(List<Sku> skus) {
        ListDto leaderSkusDto = new ListDto();

        for (Sku sku : skus) {
            leaderSkusDto.add(new LeaderSkuDto(sku));
        }

        return leaderSkusDto;
    }

    private Sku sku;

    public long getProductId() {
        return sku.getProductId();
    }

    public long getSkuId() {
        return sku.getId();
    }

    public String getTime() {
        return sku.getTime();
    }

    public String getDesc() {
        return sku.getDesc();
    }

    public LeaderSkuDto(Sku sku) {
        this.sku = sku;
    }
}
