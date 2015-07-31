package cn.momia.service.web.ctrl.deal;

import cn.momia.common.service.util.HttpUtil;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.common.web.misc.RequestUtil;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.facade.OrderInfoFields;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.user.base.User;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.web.ctrl.AbstractController;
import cn.momia.service.web.ctrl.product.dto.MiniProductDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
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
        if (!order.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != productId ||
                order.getSkuId() != skuId) return ResponseMessage.FAILED("无效的订单");

        Product product = productServiceFacade.get(order.getProductId(), true);
        Sku sku = productServiceFacade.getSku(order.getSkuId());
        if (!product.exists() || !sku.exists() || sku.isClosed(new Date())) return ResponseMessage.FAILED("无效的订单");

        String userCouponIdStr = request.getParameter("coupon");
        long userCouponId = StringUtils.isBlank(userCouponIdStr) ? 0 : Long.valueOf(userCouponIdStr);
        Coupon coupon = useCoupon(user.getId(), order, userCouponId);

        Map<String, String> orderInfo = extraOrderInfo(request, user, order, product, coupon, payType);
        PrepayResult prepayResult = dealServiceFacade.prepay(orderInfo, payType);

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

    private Map<String, String> extraOrderInfo(HttpServletRequest request, User user, Order order, Product product, Coupon coupon, int payType) {
        Map<String, String> orderInfo = HttpUtil.extractParams(request.getParameterMap());
        orderInfo.put(OrderInfoFields.ORDER_ID, String.valueOf(order.getId()));
        orderInfo.put(OrderInfoFields.PRODUCT_ID, String.valueOf(product.getId()));
        orderInfo.put(OrderInfoFields.PRODUCT_TITLE, product.getTitle());

        if (payType == Payment.Type.WECHATPAY)
            orderInfo.put(OrderInfoFields.TOTAL_FEE, String.valueOf((int) (promoServiceFacade.calcTotalFee(order.getTotalFee(), coupon).floatValue() * 100)));
        else
            orderInfo.put(OrderInfoFields.TOTAL_FEE, String.valueOf(promoServiceFacade.calcTotalFee(order.getTotalFee(), coupon)));

        orderInfo.put(OrderInfoFields.USER_IP, RequestUtil.getRemoteIp(request));
        orderInfo.put(OrderInfoFields.UTOKEN, user.getToken());

        return orderInfo;
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
        if (!order.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != productId ||
                order.getSkuId() != skuId) return ResponseMessage.FAILED("无效的订单");

        Product product = productServiceFacade.get(order.getProductId(), true);
        Sku sku = productServiceFacade.getSku(order.getSkuId());
        if (!product.exists() || !sku.exists() || sku.isClosed(new Date())) return ResponseMessage.FAILED("无效的订单");

        BigDecimal totalFee = order.getTotalFee();
        if (userCouponId != null && userCouponId > 0) {
            Coupon coupon = useCoupon(user.getId(), order, userCouponId);
            totalFee = promoServiceFacade.calcTotalFee(totalFee, coupon);

            if (coupon.exists() && !promoServiceFacade.useUserCoupon(user.getId(), orderId, userCouponId)) return ResponseMessage.FAILED("支付失败");
        }

        if (totalFee.compareTo(new BigDecimal(0)) != 0 ||
                !dealServiceFacade.prepayOrder(orderId) ||
                !dealServiceFacade.payOrder(orderId)) return ResponseMessage.FAILED("支付失败");

        return new ResponseMessage(new MiniProductDto(productServiceFacade.get(productId, true)));
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseMessage checkPayment(@RequestParam String utoken,
                                        @RequestParam(value = "oid") long orderId,
                                        @RequestParam(value = "pid") long productId,
                                        @RequestParam(value = "sid") long skuId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!dealServiceFacade.check(user.getId(), orderId, productId, skuId)) return ResponseMessage.FAILED("支付失败");

        return new ResponseMessage(new MiniProductDto(productServiceFacade.get(productId, true)));
    }
}
