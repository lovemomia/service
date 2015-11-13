package cn.momia.api.user;

import cn.momia.api.user.dto.ChildDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.HttpUriRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChildServiceApi extends ServiceApi {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public UserDto add(List<ChildDto> children) {
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/user/child"), JSON.toJSONString(children));
        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public ChildDto get(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/child/%d", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), ChildDto.class);
    }

    public List<ChildDto> list(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/user/child"), builder.build());

        return CastUtil.toList((JSON) executeRequest(request), ChildDto.class);
    }

    public UserDto updateAvatar(String utoken, long childId, String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/avatar", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateName(String utoken, long childId, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/name", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateSex(String utoken, long childId, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/sex", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto updateBirthday(String utoken, long childId, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", DATE_FORMAT.format(birthday));
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/user/child/%d/birthday", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }

    public UserDto delete(String utoken, long childId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/user/child/%d", childId), builder.build());

        return CastUtil.toObject((JSON) executeRequest(request), UserDto.class);
    }
}
