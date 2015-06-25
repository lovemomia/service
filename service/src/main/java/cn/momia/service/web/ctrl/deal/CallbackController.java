package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.factory.CallbackParamFactory;
import cn.momia.service.deal.payment.gateway.factory.PaymentGatewayFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/callback")
public class CallbackController {
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public ResponseMessage alipayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.ALIPAY);
    }

    private ResponseMessage callback(HttpServletRequest request, int payType) {
        CallbackParam callbackParam = CallbackParamFactory.create(request.getParameterMap(), payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        CallbackResult callbackResult = gateway.callback(callbackParam);

        if (callbackResult.isSuccessful()) return ResponseMessage.SUCCESS;
        return ResponseMessage.FAILED;
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public ResponseMessage wechatpayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.WECHATPAY);
    }
}
