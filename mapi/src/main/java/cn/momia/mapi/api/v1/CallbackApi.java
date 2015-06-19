package cn.momia.mapi.api.v1;

import cn.momia.common.encrypt.XmlUtil;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.Xml;
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

    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public ResponseMessage alipayCallback(HttpServletRequest request) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST, produces = "application/xml")
    public Xml wechatpayCallback(HttpServletRequest request) {
        try {
            Map<String, String> params = XmlUtil.doXmlParse(IOUtils.toString(request.getInputStream()));
            ResponseMessage response = executeRequest(new MomiaHttpPostRequest(dealServiceUrl("callback/wechatpay"), params));
            if (response.getErrno() == ErrorCode.SUCCESS) return new Xml("SUCCESS", "OK");
        } catch (Exception e) {
            LOGGER.error("wechat pay callback error", e);
        }

        return new Xml("FAIL", "ERROR");
    }
}
