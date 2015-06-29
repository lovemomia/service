package cn.momia.service.base.product.sku;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class SkuProperty implements Serializable {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public SkuProperty(JSONObject propertyJson) {
        this.name = propertyJson.getString("name");
        this.value = propertyJson.getString("value");
    }
}
