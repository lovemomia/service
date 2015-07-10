package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ProductDetailDto extends ProductDto {
    private JSONObject customers;

    public JSONObject getCustomers() {
        return customers;
    }

    public ProductDetailDto(JSONObject productJson, JSONObject customersJson) {
        super(ProductUtil.extractProductData(productJson, true));
        this.customers = processAvatars(customersJson);
    }

    private JSONObject processAvatars(JSONObject customersJson) {
        JSONArray avatarsJson = customersJson.getJSONArray("avatars");
        if (avatarsJson != null) {
            for (int i = 0; i < avatarsJson.size(); i++) {
                String avatar = avatarsJson.getString(i);
                avatarsJson.set(i, ImageFile.url(avatar));
            }
        }

        return customersJson;
    }
}
