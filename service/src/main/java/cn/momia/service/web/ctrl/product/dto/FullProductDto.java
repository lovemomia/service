package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.product.Product;
import cn.momia.service.product.ProductImage;
import cn.momia.service.web.ctrl.dto.Dto;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FullProductDto extends BaseProductDto implements Dto {
    public String getCrowd() {
        return product.getCrowd();
    }

    public List<String> getImgs() {
        List<String> imgs = new ArrayList<String>();
        for (ProductImage productImage : product.getImgs()) imgs.add(productImage.getUrl());

        return imgs;
    }

    public JSONArray getContent() {
        return product.getContent();
    }

    public FullProductDto(Product product) {
        super(product);
    }

    public FullProductDto(Product product, boolean withSku) {
        super(product, withSku);
    }
}
