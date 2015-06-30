package cn.momia.mapi.api.v1.dto.base;

import cn.momia.mapi.img.ImageFile;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class UserDto implements Dto {
    public static class Other extends UserDto {
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
    @JSONField(format = "yyyy-MM-dd") private Date birthday;
    private String city;
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

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public UserDto(JSONObject userJson) {
        this.token = userJson.getString("token");
        this.nickName = userJson.getString("nickName");
        this.mobile = userJson.getString("mobile");
        this.avatar = ImageFile.url(userJson.getString("avatar"));
        this.name = userJson.getString("name");
        this.sex = userJson.getString("sex");
        this.birthday = userJson.getDate("birthday");
        this.city = userJson.getString("city");
        this.address = userJson.getString("address");
    }
}
