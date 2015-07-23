package cn.momia.mapi.api.v1.dto.product;

import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class PlaceOrderDto implements Dto {
    private JSONObject contacts;
    private JSONArray skus;

    public JSONObject getContacts() {
        return contacts;
    }

    public JSONArray getSkus() {
        return skus;
    }

    public PlaceOrderDto(JSONObject contactsJson, JSONArray skusJson) {
        this.contacts = contactsJson;
        this.skus = skusJson;
    }
}
