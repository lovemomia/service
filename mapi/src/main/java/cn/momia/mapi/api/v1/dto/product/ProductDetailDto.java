package cn.momia.mapi.api.v1.dto.product;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import com.alibaba.fastjson.JSONArray;
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

        this.customers = processAvatars(customersJson);
        this.url = ProductUtil.buildUrl(getId());
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
