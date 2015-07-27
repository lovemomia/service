package cn.momia.service.web.ctrl.deal;

import cn.momia.common.misc.HttpUtil;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/callback")
public class CallbackController extends AbstractController {
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public ResponseMessage alipayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.ALIPAY);
    }

    private ResponseMessage callback(HttpServletRequest request, int payType) {
        if (dealServiceFacade.callback(HttpUtil.extractParams(request.getParameterMap()), payType))
            return ResponseMessage.SUCCESS;
        return ResponseMessage.FAILED;
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public ResponseMessage wechatpayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.WECHATPAY);
    }
}
