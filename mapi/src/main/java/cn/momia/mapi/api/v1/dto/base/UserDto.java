package cn.momia.mapi.api.v1.dto.base;

import cn.momia.common.secret.MobileEncryptor;
import cn.momia.mapi.api.v1.dto.composite.ListDto;
import cn.momia.common.web.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class UserDto implements Dto {
    public static class Other extends UserDto {
        public Other(JSONObject userPackJson) {
            super(userPackJson);
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
    private boolean hasPassword;
    private String avatar;
    private String name;
    private String sex;
    @JSONField(format = "yyyy-MM-dd") private Date birthday;
    private String city;
    private String address;
    private ListDto children;

    private Integer userCouponId;

    public String getToken() {
        return token;
    }

    public String getNickName() {
        return nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public boolean isHasPassword() {
        return hasPassword;
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

    public ListDto getChildren() {
        return children;
    }

    public Integer getUserCouponId() {
        return userCouponId;
    }

    public UserDto(JSONObject userPackJson) {
        this(userPackJson, false);
    }

    public UserDto(JSONObject userPackJson, boolean showToken) {
        JSONObject userJson = userPackJson.getJSONObject("user");
        JSONArray childrenJson = userPackJson.getJSONArray("children");

        if (showToken) this.token = userJson.getString("token");
        this.nickName = userJson.getString("nickName");
        this.mobile = MobileEncryptor.encrypt(userJson.getString("mobile"));
        this.hasPassword = userJson.getBoolean("hasPassword");
        this.avatar = ImageFile.url(userJson.getString("avatar"));
        this.name = userJson.getString("name");
        this.sex = userJson.getString("sex");
        this.birthday = userJson.getDate("birthday");
        this.city = userJson.getString("city");
        this.address = userJson.getString("address");

        this.children = new ListDto();
        for (int i = 0; i < childrenJson.size(); i++) {
            JSONObject childJson = childrenJson.getJSONObject(i);
            this.children.add(new ParticipantDto(childJson));
        }

        this.userCouponId = userPackJson.getInteger("userCouponId");
    }
}
