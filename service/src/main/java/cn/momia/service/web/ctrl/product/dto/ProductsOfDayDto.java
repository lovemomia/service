package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.web.ctrl.dto.Dto;
import cn.momia.service.web.ctrl.dto.ListDto;
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
