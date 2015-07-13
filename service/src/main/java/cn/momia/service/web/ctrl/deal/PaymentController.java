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
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
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
    @Autowired private CouponService couponService;

    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public ResponseMessage prepayAlipay(HttpServletRequest request) {
        return prepay(request, Payment.Type.ALIPAY);
    }

    private ResponseMessage prepay(HttpServletRequest request, int payType) {
        String utoken = request.getParameter("utoken");
        long orderId = Long.valueOf(request.getParameter("oid"));
        long productId = Long.valueOf(request.getParameter("pid"));
        long skuId = Long.valueOf(request.getParameter("sid"));
        if (StringUtils.isBlank(utoken) || orderId <= 0 || productId <= 0 || skuId <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = orderService.get(orderId);
        Product product = productService.get(productId);
        if (!order.exists() || !product.exists()) return ResponseMessage.BAD_REQUEST;
        if (order.getCustomerId() != user.getId() || order.getSkuId() != skuId) return ResponseMessage.BAD_REQUEST;

        Coupon coupon = getCoupon(user.getId(), orderId, request);
        if (coupon.invalid()) return ResponseMessage.FAILED("无效的优惠券");

        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        Map<String, String> params = gateway.extractPrepayParams(request, order, product, coupon);
        PrepayParam prepayParam = PrepayParamFactory.create(params, payType);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        if (!prepayResult.isSuccessful()) return ResponseMessage.FAILED;
        return new ResponseMessage(prepayResult);
    }

    private Coupon getCoupon(long userId, long orderId, HttpServletRequest request) {
        String couponStr = request.getParameter("coupon");
        if (!StringUtils.isBlank(couponStr)) {
            long userCouponId = Long.valueOf(couponStr);
            if (userCouponId <= 0) return Coupon.INVALID_COUPON;

            UserCoupon userCoupon = couponService.lockUserCoupon(userId, orderId, userCouponId);
            if (!userCoupon.exists()) return Coupon.INVALID_COUPON;

            Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());
            if (!coupon.exists()) return Coupon.INVALID_COUPON;

            return coupon;
        }

        return Coupon.NOT_EXIST_COUPON;
    }

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(HttpServletRequest request) {
        return prepay(request, Payment.Type.WECHATPAY);
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseMessage checkPayment(@RequestParam String utoken,
                                        @RequestParam(value = "oid") long orderId,
                                        @RequestParam(value = "pid") long productId,
                                        @RequestParam(value = "sid") long skuId) {
        if (StringUtils.isBlank(utoken) ||
                orderId <= 0 ||
                productId <= 0 ||
                skuId <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        long userId = user.getId();
        if (!orderService.check(orderId, userId, productId, skuId)) return new ResponseMessage("FAIL");

        return new ResponseMessage("OK");
    }
}
