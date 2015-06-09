package cn.momia.service.base.product.factory;

import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.concrete.ActivityProduct;
import com.alibaba.fastjson.JSONObject;

public class ProductFactory {
    public static Product create(JSONObject jsonObject) {
        int category = jsonObject.getInteger("category");
        switch (category) {
            case Product.Type.ACTIVITY:
                return new ActivityProduct(jsonObject);
            default:
                throw new RuntimeException("invalid product: " + jsonObject.toJSONString());
        }
    }
}
