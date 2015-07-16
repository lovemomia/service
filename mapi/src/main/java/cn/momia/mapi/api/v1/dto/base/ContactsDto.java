package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

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
        String name = userJson.getString("name");
        if (StringUtils.isBlank(name)) name = userJson.getString("nickName");

        this.name = name;
        this.mobile = userJson.getString("mobile");
    }
}
