package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.ContactsDto;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.SkuDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
            skus.add(new SkuDto(skusJson.getJSONObject(i)));
        }

        return skus;
    }
}
