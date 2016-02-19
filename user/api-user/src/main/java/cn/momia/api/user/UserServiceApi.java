package cn.momia.api.user;

import cn.momia.api.user.dto.Contact;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import cn.momia.common.core.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class UserServiceApi extends HttpServiceApi {
    public User get(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/user"), builder.build()), User.class);
    }

    public User get(long userId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/user/%d", userId)), User.class);
    }

    public User getByMobile(String mobile) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("mobile", mobile);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/user/mobile"), builder.build()), User.class);
    }

    public User getByInviteCode(String inviteCode) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("invite", inviteCode);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/user/invite"), builder.build()), User.class);
    }

    public List<User> list(Collection<Long> userIds, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("type", type);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/user/list"), builder.build()), User.class);
    }

    public User updateNickName(String utoken, String nickName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("nickname", nickName);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/nickname"), builder.build()), User.class);
    }

    public User updateAvatar(String utoken, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/avatar"), builder.build()), User.class);
    }

    public User updateCover(String utoken, String cover) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("cover", cover);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/cover"), builder.build()), User.class);
    }

    public User updateName(String utoken, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/name"), builder.build()), User.class);
    }

    public User updateSex(String utoken, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/sex"), builder.build()), User.class);
    }

    public User updateBirthday(String utoken, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", TimeUtil.SHORT_DATE_FORMAT.format(birthday));
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/birthday"), builder.build()), User.class);
    }

    public User updateCity(String utoken, int cityId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", cityId);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/city"), builder.build()), User.class);
    }

    public User updateRegion(String utoken, int regionId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("region", regionId);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/region"), builder.build()), User.class);
    }

    public User updateAddress(String utoken, String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/address"), builder.build()), User.class);
    }

    public User updateImToken(String utoken, String imToken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("imtoken", imToken);
        return executeReturnObject(MomiaHttpRequestBuilder.PUT(url("/user/imtoken"), builder.build()), User.class);
    }

    public Contact getContact(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/user/contact"), builder.build()), Contact.class);
    }

    public boolean setPayed(long userId) {
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/user/%d/payed", userId)), Boolean.class);
    }

    public void notifyBatch(List<Long> userIds, String message) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("message", message);
        execute(MomiaHttpRequestBuilder.POST(url("/user/notify/batch"), builder.build()));
    }
}
