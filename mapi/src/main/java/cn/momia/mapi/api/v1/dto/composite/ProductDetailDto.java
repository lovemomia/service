package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProductDetailDto implements Dto {
    public static class Customers implements Dto {
        public String text;
        public List<String> avatars;
    }

    private ProductDto productDto;
    private Customers customers;

    public long getId() {
        return productDto.getId();
    }

    public String getCover() {
        return productDto.getCover();
    }

    public String getTitle() {
        return productDto.getTitle();
    }

    public int getJoined() {
        return productDto.getJoined();
    }

    public BigDecimal getPrice() {
        return productDto.getPrice();
    }

    public String getCrowd() {
        return productDto.getCrowd();
    }

    public String getScheduler() {
        return productDto.getScheduler();
    }

    public String getAddress() {
        return productDto.getAddress();
    }

    public String getPoi() {
        return productDto.getPoi();
    }

    @JSONField(format = "yyyy-MM-dd hh:mm:ss") public Date getStartTime() {
        return productDto.getStartTime();
    }

    @JSONField(format = "yyyy-MM-dd hh:mm:ss") public Date getEndTime() {
        return productDto.getEndTime();
    }

    public boolean isSoldOut() {
        return productDto.isSoldOut();
    }

    public List<String> getImgs() {
        return productDto.getImgs();
    }

    public JSONArray getContent() {
        return productDto.getContent();
    }

    public void setProductDto(ProductDto productDto) {
        this.productDto = productDto;
    }

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }
}
