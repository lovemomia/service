package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import cn.momia.service.deal.payment.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.deal.payment.gateway.factory.PrepayParamFactory;
import cn.momia.service.web.ctrl.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(HttpServletRequest request) {
        String utoken = request.getParameter("utoken");
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        Product product = getProduct(request);
        Order order = getOrder(request);
        if (!product.exists() || !order.exists()) return new ResponseMessage(ErrorCode.FAILED, "product or(and) order does not exist");
        if (!orderService.prepay(order.getId(), user.getId())) return new ResponseMessage(ErrorCode.FAILED, "fail to prepay");

        PrepayParam prepayParam = PrepayParamFactory.create(request.getParameterMap(), product, order, Payment.Type.WECHATPAY);
        PaymentGateway gateway = PaymentGatewayFactory.create(Payment.Type.WECHATPAY);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        if (!prepayResult.isSuccessful()) {
            if (!orderService.unPrepay(order.getId(), user.getId())) {
                // TODO 需要告警
                LOGGER.error("fail to unprepay, order id: {}", order.getId());
            }
        }

        return new ResponseMessage(prepayResult);
    }

    private Product getProduct(HttpServletRequest request) {
        return productService.get(Long.valueOf(request.getParameter("productid")));
    }

    private Order getOrder(HttpServletRequest request) {
        return orderService.get(Long.valueOf(request.getParameter("orderid")));
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseMessage checkPayment(@RequestParam String utoken, @RequestParam(value = "pid") long productId, @RequestParam(value = "sid") long skuId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        long userId = user.getId();
        if (!orderService.check(userId, productId, skuId)) return new ResponseMessage("FAIL");

        return new ResponseMessage("OK");
    }
}
