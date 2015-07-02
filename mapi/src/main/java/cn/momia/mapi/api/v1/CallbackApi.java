package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.AbstractApi;
import cn.momia.mapi.api.misc.Xml;
import cn.momia.common.misc.XmlUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/v1/callback")
public class CallbackApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackApi.class);

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST, produces = "application/xml")
    public Xml wechatpayCallback(HttpServletRequest request) {
        try {
            Map<String, String> params = XmlUtil.xmlToParams(IOUtils.toString(request.getInputStream()));
            ResponseMessage response = executeRequest(MomiaHttpRequest.POST(dealServiceUrl("callback/wechatpay"), params));
            if (response.getErrno() == ErrorCode.SUCCESS) return new Xml("SUCCESS", "OK");
        } catch (Exception e) {
            LOGGER.error("wechat pay callback error", e);
        }

        return new Xml("FAIL", "ERROR");
    }
}
