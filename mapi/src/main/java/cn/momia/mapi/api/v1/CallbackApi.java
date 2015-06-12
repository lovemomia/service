package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/callback")
public class CallbackApi extends AbstractApi {
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public ResponseMessage alipayCallback(HttpServletRequest request) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST, produces = "application/xml")
    public ResponseMessage wechatpayCallback(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        // TODO
        MomiaHttpRequest momiaHttpRequest = new MomiaHttpPostRequest("callback", true, dealServiceUrl("callback", "wechatpay"), params);

        return executeRequest(momiaHttpRequest);
    }
}
