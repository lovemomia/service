package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.UserDto;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1/auth")
public class AuthApi extends AbstractApi {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[0-9]{10}$");

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile)  {
        if (isInvalidMobile(mobile)) return ResponseMessage.FAILED("invalid mobile");

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("mobile", mobile);
        MomiaHttpRequest request = MomiaHttpRequest.POST(baseServiceUrl("auth/send"), builder.build());

        return executeRequest(request);
    }

    private boolean isInvalidMobile(String mobile) {
        return !MOBILE_PATTERN.matcher(mobile).find();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam String code) {
        if (isInvalidMobile(mobile)) return ResponseMessage.FAILED("invalid mobile");

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(baseServiceUrl("auth/login"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new UserDto((JSONObject) data);
            }
        });
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMessage register(@RequestParam String nickName, @RequestParam String mobile, @RequestParam String code) {
        if (isInvalidMobile(mobile)) return ResponseMessage.FAILED("invalid mobile");

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("nickName", nickName)
                .add("mobile", mobile)
                .add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(baseServiceUrl("auth/register"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new UserDto((JSONObject) data);
            }
        });
    }
}
