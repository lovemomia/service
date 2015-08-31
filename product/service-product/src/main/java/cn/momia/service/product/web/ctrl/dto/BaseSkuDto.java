package cn.momia.service.product.web.ctrl.dto;

import cn.momia.service.product.sku.Sku;
import cn.momia.api.user.leader.Leader;
import cn.momia.service.base.web.ctrl.dto.Dto;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

public class BaseSkuDto implements Dto {
    protected Sku sku;
    protected Leader leader;

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

    public int getStock() {
        int stock = sku.getUnlockedStock();
        return stock < 0 ? 0 : stock;
    }

    public BigDecimal getMinPrice() {
        return sku.getMinPrice();
    }

    public BigDecimal getMinOriginalPrice() {
        return sku.getMinOriginalPrice();
    }

    public String getTime() {
        return sku.getFormatedTime();
    }

    public int getPlaceId() {
        return sku.getPlaceId();
    }

    public String getPlaceName() {
        return sku.getPlaceName();
    }

    public String getAddress() {
        return sku.getAddress();
    }

    public boolean isHasLeader() {
        return sku.hasLeader() || !sku.isNeedLeader();
    }

    public String getLeaderInfo() {
        if (!sku.isNeedLeader()) return "本场不需要领队";
        if (leader == null || StringUtils.isBlank(leader.getName())) return "";
        return leader.getName() + "已成为领队";
    }

    public boolean isFull() {
        return sku.isFull();
    }

    public boolean isFinished() {
        return sku.isFinished(new Date());
    }

    public boolean isClosed() {
        return sku.isClosed(new Date());
    }

    public BaseSkuDto(Sku sku) {
        this.sku = sku;
    }

    public BaseSkuDto(Sku sku, Leader leader) {
        this(sku);
        this.leader = leader;
    }
}
