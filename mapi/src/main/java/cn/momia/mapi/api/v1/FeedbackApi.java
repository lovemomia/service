package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackApi.class);

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addFeedback(@RequestParam String content, @RequestParam String email, @RequestParam(required = false) String utoken) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("content", content);
        params.put("email", email);
        if (utoken != null) params.put("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpPostRequest("feedback", true, baseServiceUrl(new Object[] { "feedback" }), params);
        try {
            JSONObject response = httpClient.execute(request);
            return ResponseMessage.formJson(response);
        } catch (Exception e) {
            LOGGER.error("fail to add feedback", e);
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add feedback");
        }
    }
}
