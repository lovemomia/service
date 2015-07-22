package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.exception.MomiaFailedException;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.product.Product;
import cn.momia.service.user.base.User;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.web.ctrl.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public ResponseMessage prepayAlipay(HttpServletRequest request) {
        return prepay(request, Payment.Type.ALIPAY);
    }

    private ResponseMessage prepay(HttpServletRequest request, int payType) {
        String utoken = request.getParameter("utoken");
        long orderId = Long.valueOf(request.getParameter("oid"));
        long productId = Long.valueOf(request.getParameter("pid"));
        long skuId = Long.valueOf(request.getParameter("sid"));

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = dealServiceFacade.getOrder(orderId);
        Product product = productServiceFacade.get(productId);
        if (!order.exists() ||
                !product.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != product.getId() ||
                order.getSkuId() != skuId) return ResponseMessage.FAILED("无效的订单");

        Coupon coupon = Coupon.NOT_EXIST_COUPON;
        String userCouponIdStr = request.getParameter("coupon");
        if (!StringUtils.isBlank(userCouponIdStr)) coupon = useCoupon(user.getId(), order, Long.valueOf(userCouponIdStr));

        PrepayResult prepayResult = dealServiceFacade.prepay(request, order, product, coupon, payType);

        if (!prepayResult.isSuccessful()) return ResponseMessage.FAILED;
        return new ResponseMessage(prepayResult);
    }

    private Coupon useCoupon(long userId, Order order, long userCouponId) {
        UserCoupon previousUserCoupon = promoServiceFacade.getNotUsedUserCouponByOrder(order.getId());
        if (userCouponId > 0) {
            if (previousUserCoupon.exists() && previousUserCoupon.getId() != userCouponId) releasePreviousUserCoupon(previousUserCoupon);
            Coupon coupon = promoServiceFacade.getCoupon(userId, order.getId(), userCouponId);

            if (!coupon.exists() ||
                    coupon.getConsumption().compareTo(order.getTotalFee()) > 0 ||
                    !promoServiceFacade.lockUserCoupon(userId, order.getId(), userCouponId))
                throw new MomiaFailedException("无效的优惠券，或使用条件不满足");

            return coupon;
        }

        if (previousUserCoupon.exists()) releasePreviousUserCoupon(previousUserCoupon);

        return Coupon.NOT_EXIST_COUPON;
    }

    private void releasePreviousUserCoupon(UserCoupon previousUserCoupon) {
        promoServiceFacade.releaseUserCoupon(previousUserCoupon.getUserId(), previousUserCoupon.getOrderId());
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
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = dealServiceFacade.getOrder(orderId);
        if (!order.exists()) return ResponseMessage.FAILED("无效的订单");

        if (order.getProductId() != productId || order.getSkuId() != skuId) return ResponseMessage.FAILED("无效的订单");

        BigDecimal totalFee = order.getTotalFee();
        if (userCouponId != null && userCouponId > 0) {
            Coupon coupon = useCoupon(user.getId(), order, userCouponId);
            totalFee = promoServiceFacade.calcTotalFee(totalFee, coupon);
        }

        if (totalFee.compareTo(new BigDecimal(0)) != 0 ||
                !promoServiceFacade.lockUserCoupon(user.getId(), orderId, userCouponId) ||
                !promoServiceFacade.useUserCoupon(user.getId(), orderId, userCouponId) ||
                !dealServiceFacade.prepayOrder(orderId) ||
                !dealServiceFacade.payOrder(orderId)) return ResponseMessage.FAILED("支付失败");

        return new ResponseMessage(productServiceFacade.get(productId));
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseMessage checkPayment(@RequestParam String utoken,
                                        @RequestParam(value = "oid") long orderId,
                                        @RequestParam(value = "pid") long productId,
                                        @RequestParam(value = "sid") long skuId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!dealServiceFacade.check(user.getId(), orderId, productId, skuId)) return ResponseMessage.FAILED("支付失败");

        return new ResponseMessage(productServiceFacade.get(productId));
    }
}
