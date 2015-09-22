package cn.momia.service.product.web.ctrl.dto;

import cn.momia.common.api.dto.Dto;
import cn.momia.common.api.dto.ListDto;
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

    public void addProduct(BaseProductDto product) {
        if (!products.contains(product)) products.add(product);
    }
}
