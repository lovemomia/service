package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthApi extends AbstractApi {
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile)  {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("mobile", mobile);
        MomiaHttpPostRequest request = new MomiaHttpPostRequest(baseServiceUrl("auth", "send"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam(value = "code") String verifyCode) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("mobile", mobile)
                .add("code", verifyCode);
        MomiaHttpPostRequest request = new MomiaHttpPostRequest(baseServiceUrl("auth", "login"), builder.build());

        return executeRequest(request);
    }
}
