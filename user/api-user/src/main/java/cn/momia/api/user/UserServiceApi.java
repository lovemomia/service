package cn.momia.api.user;

import cn.momia.api.user.dto.Contact;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class UserServiceApi extends ServiceApi {
    public User get(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User get(long userId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/%d", userId));
        return executeReturnObject(request, User.class);
    }

    public User getByMobile(String mobile) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("mobile", mobile);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/mobile"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User getByInviteCode(String inviteCode) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("invite", inviteCode);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/invite"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public List<User> list(Collection<Long> userIds, int type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("uids", StringUtils.join(userIds, ","))
                .add("type", type);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/list"), builder.build());

        return executeReturnList(request, User.class);
    }

    public User updateNickName(String utoken, String nickName) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("nickname", nickName);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/nickname"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateAvatar(String utoken, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/avatar"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateCover(String utoken, String cover) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("cover", cover);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/cover"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateName(String utoken, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/name"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateSex(String utoken, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/sex"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateBirthday(String utoken, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", TimeUtil.SHORT_DATE_FORMAT.format(birthday));
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/birthday"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateCity(String utoken, int cityId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", cityId);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/city"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateRegion(String utoken, int regionId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("region", regionId);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/region"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public User updateAddress(String utoken, String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/address"), builder.build());

        return executeReturnObject(request, User.class);
    }

    public Contact getContact(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/contact"), builder.build());

        return executeReturnObject(request, Contact.class);
    }

    public boolean setPayed(long userId) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/user/%d/payed", userId));
        return executeReturnObject(request, Boolean.class);
    }
}
