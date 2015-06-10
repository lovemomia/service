package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.SignParam;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/sign/alipay", method = RequestMethod.POST)
    public ResponseMessage signAlipay()
    {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/sign/wechatpay", method = RequestMethod.POST)
    public ResponseMessage signWechatpay()
    {
        return new ResponseMessage("TODO");
    }

    private SignParam buildSignParam() {
        // TODO
        return null;
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseMessage checkPayment() {
        return new ResponseMessage("TODO");
    }
}
