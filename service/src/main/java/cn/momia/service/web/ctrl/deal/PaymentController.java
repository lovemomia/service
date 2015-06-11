package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.SignParam;
import cn.momia.service.deal.payment.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.deal.payment.gateway.factory.SignParamFactory;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/sign/alipay", method = RequestMethod.POST)
    public ResponseMessage signAlipay(HttpServletRequest request) {
        return new ResponseMessage(sign(request, Payment.Type.ALIPAY));
    }

    private String sign(HttpServletRequest request, int payType) {
        SignParam signParam = SignParamFactory.create(request.getParameterMap(), payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);

        return gateway.sign(signParam);
    }

    @RequestMapping(value = "/sign/wechatpay", method = RequestMethod.POST)
    public ResponseMessage signWechatpay(HttpServletRequest request) {
        return new ResponseMessage(sign(request, Payment.Type.WECHATPAY));
    }

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(HttpServletRequest request) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseMessage checkPayment() {
        // TODO
        return new ResponseMessage("TODO");
    }
}
