package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;

public class ContactsDto implements Dto {
    private String name;
    private String mobile;

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public ContactsDto(JSONObject userJson) {
        this.name = userJson.getString("name");
        this.mobile = userJson.getString("mobile");
    }
}
