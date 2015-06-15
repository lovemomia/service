package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/feedback")
public class FeedbackApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addFeedback(@RequestParam String content, @RequestParam String email, @RequestParam(required = false) String utoken) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("content", content);
        params.put("email", email);
        if (utoken != null) params.put("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpPostRequest("feedback", true, baseServiceUrl("feedback"), params);

        return executeRequest(request);
    }
}
