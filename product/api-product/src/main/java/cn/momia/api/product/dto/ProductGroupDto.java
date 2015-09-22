package cn.momia.api.product.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductGroupDto {
    @JSONField(format = "yyyy-MM-dd") private Date date;
    private List<ProductDto> products = new ArrayList<ProductDto>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public void addProduct(ProductDto productDto) {
        products.add(productDto);
    }
}
