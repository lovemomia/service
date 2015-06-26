package cn.momia.mapi.api.v1.dto.base;

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

    public static class Other extends Own {
        public Other(JSONObject userJson) {
            super(userJson);
        }

        public String getToken() {
            return "";
        }

        public String getAddress() {
            return "";
        }
    }

    private String token;
    private String nickName;
    private String mobile;
    private String avatar;
    private String name;
    private String sex;
    private Date birthday;
    private int cityId;
    private String address;

    public String getToken() {
        return token;
    }

    public String getNickName() {
        return nickName;
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

    protected UserDto(JSONObject userJson) {
        this.token = userJson.getString("token");
        this.nickName = userJson.getString("nickName");
        this.mobile = userJson.getString("mobile");
        this.avatar = ImageFile.url(userJson.getString("avatar"));
        this.name = userJson.getString("name");
        this.sex = userJson.getString("sex");
        this.birthday = userJson.getDate("birthday");
        this.cityId = userJson.getInteger("cityId");
        this.address = userJson.getString("address");
    }
}
