package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.web.util.MetaUtil;
import cn.momia.service.product.facade.Product;
import cn.momia.service.web.ctrl.dto.Dto;
import cn.momia.service.web.ctrl.dto.ListDto;

import java.math.BigDecimal;
import java.util.List;

public class BaseProductDto extends MiniProductDto implements Dto {
    private boolean withSku = false;
    private boolean favored = false;

    private String scheduler;

    public boolean isFavored() {
        return favored;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }

    public String getCover() {
        return product.getCover();
    }

    public int getJoined() {
        return product.getJoined();
    }

    public String getScheduler() {
        return scheduler == null ? product.getScheduler() : scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public List<String> getTags() {
        return product.getTags();
    }

    public String getRegion() {
        return MetaUtil.getRegionName(product.getRegionId());
    }

    public String getAddress() {
        return product.getAddress();
    }

    public String getPoi() {
        return product.getPoi();
    }

    public BigDecimal getPrice() {
        return product.getMinPrice();
    }

    public boolean isSoldOut() {
        return product.isSoldOut();
    }

    public boolean isOpened() {
        return product.isOpened();
    }

    public ListDto getSkus() {
        return withSku ? SkuDto.toSkusDto(product.getSkus()) : null;
    }

    public BaseProductDto(Product product) {
        super(product);
    }

    public BaseProductDto(Product product, boolean withSku) {
        this(product);
        this.withSku = withSku;
    }
}
