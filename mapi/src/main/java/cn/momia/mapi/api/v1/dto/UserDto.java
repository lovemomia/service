package cn.momia.mapi.api.v1.dto;

import cn.momia.mapi.img.ImageFile;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class UserDto implements Dto {
    public static class Own extends UserDto {
        public Own(JSONObject userJson) {
            super(userJson);
        }

        public String getMobile() {
            return super.getMobile().substring(0, 3) + "****" + super.getMobile().substring(7);
        }
    }

    public static class Other extends UserDto {
        public Other(JSONObject userJson) {
            super(userJson);
        }

        public String getToken() {
            return "";
        }

        public String getMobile() {
            return super.getMobile().substring(0, 3) + "****" + super.getMobile().substring(7);
        }

        public String getAddress() {
            return "";
        }
    }

    private String token;
    private String mobile;
    private String avatar;
    private String name;
    private String sex;
    private Date birthday;
    private int cityId;
    private String address;
    private String nickName;

    public String getToken() {
        return token;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public int getCityId() {
        return cityId;
    }

    public String getAddress() {
        return address;
    }
    public String getNickName() { return nickName; }

    protected UserDto(JSONObject userJson) {
        this.token = userJson.getString("token");
        this.mobile = userJson.getString("mobile");
        this.avatar = ImageFile.url(userJson.getString("avatar"));
        this.name = userJson.getString("name");
        this.sex = userJson.getString("sex");
        this.birthday = userJson.getDate("birthday");
        this.cityId = userJson.getInteger("cityId");
        this.address = userJson.getString("address");
        this.nickName = userJson.getString("nickName");
    }
}
