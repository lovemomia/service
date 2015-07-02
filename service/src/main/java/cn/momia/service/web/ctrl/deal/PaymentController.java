package cn.momia.service.web.ctrl.deal;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(HttpServletRequest request) {
        String utoken = request.getParameter("utoken");
        User user = userService.getByToken(utoken);
        if (!user.exists()) ResponseMessage.FAILED("user not exists");

        Order order = getOrder(request);
        Product product = getProduct(request);
        if (!order.exists() || !product.exists()) return ResponseMessage.FAILED("order or(and) product does not exist");

        PaymentGateway gateway = PaymentGatewayFactory.create(Payment.Type.WECHATPAY);
        Map<String, String> params = gateway.extractPrepayParams(request.getParameterMap(), order, product);
        PrepayParam prepayParam = PrepayParamFactory.create(params, Payment.Type.WECHATPAY);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        return new ResponseMessage(prepayResult);
    }

    private Order getOrder(HttpServletRequest request) {
        return orderService.get(Long.valueOf(request.getParameter("oid")));
    }

    private Product getProduct(HttpServletRequest request) {
        return productService.get(Long.valueOf(request.getParameter("pid")));
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseMessage checkPayment(@RequestParam String utoken, @RequestParam(value = "oid") long orderId, @RequestParam(value = "pid") long productId, @RequestParam(value = "sid") long skuId) {
        if (StringUtils.isBlank(utoken) || productId <= 0 || skuId <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.FAILED("user not exists");

        long userId = user.getId();
        if (!orderService.check(orderId, userId, productId, skuId)) return new ResponseMessage("FAIL");

        return new ResponseMessage("OK");
    }
}
