package cn.momia.api.user;

import cn.momia.api.user.dto.ContactDto;
import cn.momia.api.user.dto.UserChildDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class UserServiceApi extends ServiceApi {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public UserDto register(String nickName, String mobile, String password, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("nickname", nickName)
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("auth/register"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto login(String mobile, String password) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("auth/login"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto loginByCode(String mobile, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("auth/login/code"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updatePassword(String mobile, String password, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("auth/password"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto get(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("user"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto get(long userId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("user", userId));
        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public boolean exists(long userId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("user", userId, "exists"));
        return (Boolean) executeRequest(request);
    }

    public UserDto updateNickName(String utoken, String nickName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("nickname", nickName);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/nickname"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateAvatar(String utoken, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/avatar"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateName(String utoken, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/name"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateSex(String utoken, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/sex"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateBirthday(String utoken, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", DATE_FORMAT.format(birthday));
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/birthday"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateCity(String utoken, int cityId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", cityId);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/city"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateRegion(String utoken, int regionId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("region", regionId);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/region"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateAddress(String utoken, String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/address"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public ContactDto getContact(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("user/contact"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), ContactDto.class);
    }

    public List<UserDto> list(Collection<Long> userIds, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("type", type);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("user/list"), builder.build());

        return CastUtil.toList((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto addChildren(List<UserChildDto> children) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("user/child"), JSON.toJSONString(children));
        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserChildDto getChild(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("user/child", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserChildDto.class);
    }

    public UserDto updateChildAvatar(String utoken, long childId, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/child", childId, "avatar"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateChildName(String utoken, long childId, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/child", childId, "name"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateChildSex(String utoken, long childId, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/child", childId, "sex"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateChildBirthday(String utoken, long childId, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", DATE_FORMAT.format(birthday));
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("user/child", childId, "birthday"), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto deleteChild(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("user/child", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public List<UserChildDto> listChildren(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("user/child"), builder.build());

        return CastUtil.toList((JSON) executeRequest(request), UserChildDto.class);
    }
}
