package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/feedback")
public class FeedbackV1Api extends AbstractV1Api {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addFeedback(@RequestParam(required = false) String utoken,
                                       @RequestParam String content,
                                       @RequestParam String email) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("content", content)
                .add("email", email);
        if (!StringUtils.isBlank(utoken)) builder.add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.POST(baseServiceUrl("feedback"), builder.build());

        return executeRequest(request);
    }
}
