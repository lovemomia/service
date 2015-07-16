package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public class PlaymatesDto extends ListDto {
    public PlaymatesDto(JSONArray skusPlaymatesJson) {
        for (int i = 0; i < skusPlaymatesJson.size(); i++) {
            JSONObject skuPlaymatesJson = skusPlaymatesJson.getJSONObject(i);
            JSONArray playmatesJson = skuPlaymatesJson.getJSONArray("playmates");
            for (int j = 0; j < playmatesJson.size(); j++) {
                JSONObject playmateJson = playmatesJson.getJSONObject(j);
                playmateJson.put("avatar", ImageFile.url(playmateJson.getString("avatar")));
            }
            this.add(skuPlaymatesJson);
        }
    }
}
