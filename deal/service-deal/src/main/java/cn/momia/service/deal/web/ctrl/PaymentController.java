package cn.momia.service.deal.web.ctrl;

import cn.momia.api.user.User;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.deal.facade.OrderInfoFields;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.api.product.Product;
import cn.momia.api.product.sku.Sku;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.promo.facade.PromoServiceFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController extends BaseController {
    @Autowired private DealServiceFacade dealServiceFacade;
    @Autowired private PromoServiceFacade promoServiceFacade;

    private static Map<String, String> extractParams(Map<String, String[]> httpParams) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : httpParams.entrySet()) {
            String[] values = entry.getValue();
            if (values.length <= 0) continue;
            params.put(entry.getKey(), entry.getValue()[0]);
        }

        return params;
    }

    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse prepayAlipay(HttpServletRequest request) {
        return prepay(request, Payment.Type.ALIPAY);
    }

    private MomiaHttpResponse prepay(HttpServletRequest request, int payType) {
        User user = UserServiceApi.USER.get(request.getParameter("utoken"));
        long orderId = Long.valueOf(request.getParameter("oid"));
        long productId = Long.valueOf(request.getParameter("pid"));
        long skuId = Long.valueOf(request.getParameter("sid"));

        Order order = dealServiceFacade.getOrder(orderId);
        if (!order.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != productId ||
                order.getSkuId() != skuId) return MomiaHttpResponse.FAILED("订单数据有问题，无效的订单");

        Product product = ProductServiceApi.PRODUCT.get(productId, Product.Type.MINI);
        Sku sku = ProductServiceApi.SKU.get(productId, skuId);
        if (!product.exists() || !sku.exists() || sku.isFinished()) return MomiaHttpResponse.FAILED("活动已结束或下线，不能再付款");

        String userCouponIdStr = request.getParameter("coupon");
        long userCouponId = StringUtils.isBlank(userCouponIdStr) ? 0 : Long.valueOf(userCouponIdStr);
        Coupon coupon = useCoupon(user.getId(), order, userCouponId);

        Map<String, String> orderInfo = extraOrderInfo(request, order, product, coupon, payType);
        PrepayResult prepayResult = dealServiceFacade.prepay(orderInfo, payType);

        if (!prepayResult.isSuccessful()) return MomiaHttpResponse.FAILED;
        return MomiaHttpResponse.SUCCESS(prepayResult);
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

    private Map<String, String> extraOrderInfo(HttpServletRequest request, Order order, Product product, Coupon coupon, int payType) {
        Map<String, String> orderInfo = extractParams(request.getParameterMap());
        orderInfo.put(OrderInfoFields.ORDER_ID, String.valueOf(order.getId()));
        orderInfo.put(OrderInfoFields.PRODUCT_ID, String.valueOf(product.getId()));
        orderInfo.put(OrderInfoFields.PRODUCT_TITLE, product.getTitle());

        if (payType == Payment.Type.WECHATPAY)
            orderInfo.put(OrderInfoFields.TOTAL_FEE, String.valueOf((int) (promoServiceFacade.calcTotalFee(order.getTotalFee(), coupon).floatValue() * 100)));
        else
            orderInfo.put(OrderInfoFields.TOTAL_FEE, String.valueOf(promoServiceFacade.calcTotalFee(order.getTotalFee(), coupon)));

        orderInfo.put(OrderInfoFields.USER_IP, getRemoteIp(request));

        return orderInfo;
    }

    private String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (isInvalidIp(ip)) ip = request.getHeader("X-Forwarded-For");
        if (isInvalidIp(ip)) ip = request.getHeader("Proxy-Client-IP");
        if (isInvalidIp(ip)) ip = request.getHeader("WL-Proxy-Client-IP");
        if (isInvalidIp(ip)) ip = request.getRemoteAddr();

        return ip;
    }

    private boolean isInvalidIp(String ip) {
        return StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip);
    }

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public MomiaHttpResponse prepayWechatpay(HttpServletRequest request) {
        return prepay(request, Payment.Type.WECHATPAY);
    }

    @RequestMapping(value = "/prepay/free", method = RequestMethod.POST)
    public MomiaHttpResponse prepayFree(@RequestParam String utoken,
                                        @RequestParam(value = "oid") long orderId,
                                        @RequestParam(value = "pid") long productId,
                                        @RequestParam(value = "sid") long skuId,
                                        @RequestParam(value = "coupon", required = false) Long userCouponId) {
        User user = UserServiceApi.USER.get(utoken);
        Order order = dealServiceFacade.getOrder(orderId);
        if (!order.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != productId ||
                order.getSkuId() != skuId) return MomiaHttpResponse.FAILED("订单数据有问题，无效的订单");

        if (order.isPayed()) return MomiaHttpResponse.SUCCESS;

        Product product = ProductServiceApi.PRODUCT.get(productId, Product.Type.MINI);
        Sku sku = ProductServiceApi.SKU.get(productId, skuId);
        if (!product.exists() || !sku.exists() || sku.isFinished()) return MomiaHttpResponse.FAILED("活动已结束或下线，不能再付款");

        BigDecimal totalFee = order.getTotalFee();
        if (userCouponId != null && userCouponId > 0) {
            Coupon coupon = useCoupon(user.getId(), order, userCouponId);
            totalFee = promoServiceFacade.calcTotalFee(totalFee, coupon);

            if (totalFee.compareTo(new BigDecimal(0)) != 0 ||
                    (coupon.exists() && !promoServiceFacade.useUserCoupon(user.getId(), orderId, userCouponId))) return MomiaHttpResponse.FAILED("支付失败");
        }

        if (totalFee.compareTo(new BigDecimal(0)) != 0 ||
                !dealServiceFacade.prepayOrder(orderId) ||
                !dealServiceFacade.payOrder(orderId)) return MomiaHttpResponse.FAILED("支付失败");

        if (!UserServiceApi.USER.isPayed(order.getCustomerId())) UserServiceApi.USER.setPayed(order.getCustomerId());

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public MomiaHttpResponse checkPayment(@RequestParam String utoken,
                                          @RequestParam(value = "oid") long orderId,
                                          @RequestParam(value = "pid") long productId,
                                          @RequestParam(value = "sid") long skuId) {
        User user = UserServiceApi.USER.get(utoken);
        if (!dealServiceFacade.check(user.getId(), orderId, productId, skuId)) return MomiaHttpResponse.FAILED("支付失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
