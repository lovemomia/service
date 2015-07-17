package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ProductsOfDayDto implements Dto {
    @JSONField(format = "yyyy-MM-dd") private Date date;
    private ListDto products = new ListDto();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ListDto getProducts() {
        return products;
    }

    public void addProduct(ProductDto productDto) {
        products.add(productDto);
    }
}
