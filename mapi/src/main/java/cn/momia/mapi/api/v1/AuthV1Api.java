package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.common.misc.ValidateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthV1Api extends AbstractV1Api {
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile, @RequestParam String type)  {
        if (ValidateUtil.isInvalidMobile(mobile) || ValidateUtil.notIn(type, "login", "register")) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("type", type);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/send"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMessage register(@RequestParam(value = "nickname") String nickName,
                                    @RequestParam String mobile,
                                    @RequestParam String password,
                                    @RequestParam String code) {
        if (StringUtils.isBlank(nickName) ||
                ValidateUtil.isInvalidMobile(mobile) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("nickname", nickName)
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/register"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam String password) {
        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(password)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/login"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/login/code", method = RequestMethod.POST)
    public ResponseMessage loginByCode(@RequestParam String mobile, @RequestParam String code) {
        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("auth/login/code"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public ResponseMessage updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(password) || StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("password", password)
                .add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("auth/password"), builder.build());

        return executeRequest(request);
    }
}
