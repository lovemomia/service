package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.deal.payment.gateway.factory.PrepayParamFactory;
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
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(HttpServletRequest request) {
        Product product = getProduct(request);
        Order order = getOrder(request);
        if (!product.exists() || !order.exists()) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "product or(and) order does not exist");
        if (!orderService.prepay(order.getId())) return new ResponseMessage(ErrorCode.FAILED, "no need to prepay");

        PrepayParam prepayParam = PrepayParamFactory.create(request.getParameterMap(), product, order, Payment.Type.WECHATPAY);
        PaymentGateway gateway = PaymentGatewayFactory.create(Payment.Type.WECHATPAY);

        return new ResponseMessage(gateway.prepay(prepayParam));
    }

    private Product getProduct(HttpServletRequest request) {
        return productService.get(Long.valueOf(request.getParameter("productid")));
    }

    private Order getOrder(HttpServletRequest request) {
        return orderService.get(Long.valueOf(request.getParameter("orderid")));
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseMessage checkPayment() {
        // TODO
        return new ResponseMessage("TODO");
    }
}
