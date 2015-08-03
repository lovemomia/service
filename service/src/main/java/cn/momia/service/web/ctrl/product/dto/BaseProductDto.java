package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.sku.Sku;
import cn.momia.service.web.util.MetaUtil;
import cn.momia.service.product.facade.Product;
import cn.momia.service.web.ctrl.dto.Dto;
import cn.momia.service.web.ctrl.dto.ListDto;

import java.math.BigDecimal;
import java.util.List;

public class BaseProductDto extends MiniProductDto implements Dto {
    private static final int STATUS_DELETED = 0;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_OFFLINE = 2;
    private static final int STATUS_END = 3;
    private static final int STATUS_SOLDOUT = 4;
    private static final int STATUS_FINISHED = 5;

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
        return withSku ? buildSkusDto(product.getSkus()) : null;
    }

    private ListDto buildSkusDto(List<Sku> skus) {
        ListDto skusDto = new ListDto();

        skus = Sku.filter(skus);
        for (Sku sku : skus) {
            skusDto.add(new FullSkuDto(sku));
        }

        return skusDto;
    }

    public int getStatus() {
        if (product.isFinished()) return STATUS_FINISHED;
        if (product.isEnd()) return STATUS_END;

        if (product.isOpened()) return STATUS_SOLDOUT;

        return product.getStatus();
    }

    public BaseProductDto(Product product) {
        super(product);
    }

    public BaseProductDto(Product product, boolean withSku) {
        this(product);
        this.withSku = withSku;
    }
}
