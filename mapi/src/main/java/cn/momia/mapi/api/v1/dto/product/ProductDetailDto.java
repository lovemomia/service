package cn.momia.mapi.api.v1.dto.product;

import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import com.alibaba.fastjson.JSONObject;

public class ProductDetailDto extends ProductDto {
    private JSONObject customers;
    private String url;

    public JSONObject getCustomers() {
        return customers;
    }

    public String getUrl() {
        return url;
    }

    public ProductDetailDto(JSONObject productJson, JSONObject customersJson) {
        super(ProductUtil.extractProductData(productJson, true));

        // 1.0版本根据soldOut来判断是否可以购买，为了兼容1.0版本
        if (!isOpened()) setSoldOut(true);

        this.customers = customersJson;
        this.url = ProductUtil.buildUrl(getId());
    }
}
