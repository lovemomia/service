package cn.momia.mapi.api.v1.dto.base;

import cn.momia.mapi.api.v1.dto.composite.ListDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by ysm on 15-7-7.
 */
public class CustomerDto implements Dto
{
    private long id;
    private String avatar;
    private String nickname;
    private ListDto children;

    public long getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public ListDto getChildren() {
        return children;
    }


    public CustomerDto(JSONObject jsonObject) {
        this.id = jsonObject.getLong("userId");
        this.avatar = jsonObject.getString("avatar");
        this.nickname = jsonObject.getString("nickName");
        JSONArray children  = jsonObject.getJSONArray("children");

        ListDto childrenList = new ListDto();

        for(int i=0; i<children.size(); i++) {
            JSONObject childJson = children.getJSONObject(i);
            childrenList.add(new ParticipantDto(childJson));
        }
        this.children = childrenList;
    }
}
