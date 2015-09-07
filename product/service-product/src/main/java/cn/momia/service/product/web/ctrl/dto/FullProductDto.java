package cn.momia.service.product.web.ctrl.dto;

import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.facade.ProductImage;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FullProductDto extends BaseProductDto implements Dto {
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
