package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.PaymentService;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.SignParam;
import cn.momia.service.deal.payment.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.deal.payment.gateway.factory.PrepayParamFactory;
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

    @Autowired
    private OrderService orderService;

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
        Order order = getOrder(request);
        PrepayParam prepayParam ;
        PaymentGateway gateway;
        if(order.exists()){
            prepayParam = PrepayParamFactory.create(request.getParameterMap(), order, Payment.Type.WECHATPAY);
            gateway = PaymentGatewayFactory.create(Payment.Type.WECHATPAY);
        }else{
            return new ResponseMessage(ErrorCode.NOT_FOUND,"错误信息：订单不存在...");
        }

        return new ResponseMessage(gateway.prepay(prepayParam));
    }

    private Order getOrder(HttpServletRequest request) {
        return orderService.get(new Long(request.getParameter("orderid")));
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseMessage checkPayment() {
        return new ResponseMessage("TODO");
    }
}
