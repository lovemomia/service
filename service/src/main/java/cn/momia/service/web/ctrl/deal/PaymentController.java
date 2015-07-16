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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

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

        Coupon coupon = Coupon.NOT_EXIST_COUPON;
        String userCouponIdStr = request.getParameter("coupon");
        if (!StringUtils.isBlank(userCouponIdStr)) {
            coupon = useCoupon(user.getId(), order, Long.valueOf(userCouponIdStr));
            if (coupon.invalid()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");
        }

        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        Map<String, String> params = gateway.extractPrepayParams(request, order, product, coupon);
        PrepayParam prepayParam = PrepayParamFactory.create(params, payType);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        if (!prepayResult.isSuccessful()) return ResponseMessage.FAILED;
        return new ResponseMessage(prepayResult);
    }

    private Coupon useCoupon(long userId, Order order, long userCouponId) {
        UserCoupon previousUserCoupon = couponService.getNotUsedUserCouponByOrder(order.getId());
        if (userCouponId > 0) {
            if (previousUserCoupon.exists() && previousUserCoupon.getId() != userCouponId) releasePreviousUserCoupon(previousUserCoupon);
            Coupon coupon = getCoupon(userId, order.getId(), userCouponId, order.getTotalFee());

            if (!coupon.exists()) return Coupon.INVALID_COUPON;
            if (!couponService.lockUserCoupon(userId, order.getId(), userCouponId)) return Coupon.INVALID_COUPON;

            return coupon;
        }

        if (previousUserCoupon.exists()) releasePreviousUserCoupon(previousUserCoupon);

        return Coupon.NOT_EXIST_COUPON;
    }

    private void releasePreviousUserCoupon(UserCoupon previousUserCoupon) {
        try {
            couponService.releaseUserCoupon(previousUserCoupon.getUserId(), previousUserCoupon.getOrderId());
        } catch (Exception e) {
            LOGGER.error("fail to release user coupon: {}", previousUserCoupon.getId(), e);
        }
    }

    private Coupon getCoupon(long userId, long orderId, long userCouponId, BigDecimal totalFee) {
        if (userCouponId <= 0) return Coupon.INVALID_COUPON;

        UserCoupon userCoupon = couponService.getUserCoupon(userId, orderId, userCouponId);
        if (!userCoupon.exists()) return Coupon.INVALID_COUPON;

        Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());
        if (!coupon.exists()) return Coupon.INVALID_COUPON;

        if (coupon.getConsumption().compareTo(totalFee) > 0) return Coupon.INVALID_COUPON;

        return coupon;
    }

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(HttpServletRequest request) {
        return prepay(request, Payment.Type.WECHATPAY);
    }

    @RequestMapping(value = "/prepay/free", method = RequestMethod.POST)
    public ResponseMessage prepayFree(@RequestParam String utoken,
                                      @RequestParam(value = "oid") long orderId,
                                      @RequestParam(value = "pid") long productId,
                                      @RequestParam(value = "sid") long skuId,
                                      @RequestParam(value = "coupon", required = false) Long userCouponId) {
        if (StringUtils.isBlank(utoken) ||
                orderId <= 0 ||
                productId <= 0 ||
                skuId <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = orderService.get(orderId);
        if (!order.exists()) return ResponseMessage.BAD_REQUEST;

        if (order.getProductId() != productId || order.getSkuId() != skuId) return ResponseMessage.BAD_REQUEST;

        BigDecimal totalFee = order.getTotalFee();
        if (userCouponId != null && userCouponId > 0) {
            Coupon coupon = useCoupon(user.getId(), order, userCouponId);
            if (coupon.invalid()) return ResponseMessage.FAILED("无效的优惠券，或使用条件不满足");

            totalFee = couponService.calcTotalFee(totalFee, coupon);
        }

        if (totalFee.compareTo(new BigDecimal(0)) != 0 ||
                !couponService.lockUserCoupon(user.getId(), orderId, userCouponId) ||
                !couponService.useUserCoupon(user.getId(), orderId, userCouponId) ||
                !orderService.prepay(orderId) ||
                !orderService.pay(orderId)) return ResponseMessage.FAILED("支付失败");

        return new ResponseMessage(productService.get(productId));
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
        if (!orderService.check(orderId, userId, productId, skuId)) return ResponseMessage.FAILED("支付失败");

        return new ResponseMessage(productService.get(productId));
    }
}
