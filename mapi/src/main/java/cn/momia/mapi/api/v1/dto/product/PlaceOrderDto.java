package cn.momia.mapi.api.v1.dto.product;

import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ListDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class PlaceOrderDto implements Dto {
    private ContactsDto contacts;
    private ListDto skus;

    public ContactsDto getContacts() {
        return contacts;
    }

    public ListDto getSkus() {
        return skus;
    }

    public PlaceOrderDto(JSONObject userPackJson, JSONArray skusJson) {
        this.contacts = getContacts(userPackJson);
        this.skus = getSkus(skusJson);
    }

    private ContactsDto getContacts(JSONObject userPackJson) {
        if (userPackJson == null) return null;

        JSONObject userJson = userPackJson.getJSONObject("user");
        if (userJson == null) return null;

        return new ContactsDto(userJson);
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
}
