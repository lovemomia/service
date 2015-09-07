package cn.momia.service.product.web.ctrl.dto;

import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.product.sku.Sku;

import java.math.BigDecimal;
import java.util.Date;

public class BaseSkuDto implements Dto {
    protected Sku sku;

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

    public long getLeaderId() {
        return sku.getLeaderUserId();
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
}
