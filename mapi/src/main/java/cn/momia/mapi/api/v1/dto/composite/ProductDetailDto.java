package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.base.SkuDto;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class ProductDetailDto extends ProductDto {
    private JSONObject customers;
    private String url;

    public JSONObject getCustomers() {
        return customers;
    }

    public String getUrl() {
        return url;
    }

    public ProductDetailDto(JSONObject productJson, JSONObject customersJson, JSONArray skusJson) {
        super(ProductUtil.extractProductData(productJson, true));

        ListDto skus = getSkus(skusJson);
        if (skus.size() <= 0) setOpened(false);

        // 1.0版本根据soldOut来判断是否可以购买，为了兼容1.0版本
        if (!isOpened()) setSoldOut(true);

        this.customers = processAvatars(customersJson);
        this.url = ProductUtil.buildUrl(getId());
    }

    private ListDto getSkus(JSONArray skusJson) {
        ListDto skus = new ListDto();
        for (int i = 0; i < skusJson.size(); i++) {
            SkuDto skuDto = new SkuDto(skusJson.getJSONObject(i));
            if (skuDto.getEndTime().before(new Date()) ||
                    (skuDto.getType() != 1 && skuDto.getStock() <= 0)) continue;
            skus.add(skuDto);
        }

        return skus;
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
