package cn.momia.service.deal.web.ctrl;

import cn.momia.api.user.User;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.deal.gateway.ClientType;
import cn.momia.service.deal.gateway.PaymentGateway;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.deal.order.Payment;
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

@RestController
@RequestMapping("/payment")
public class PaymentController extends BaseController {
    @Autowired private OrderService orderService;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse prepayAlipay(HttpServletRequest request) {
        return prepay(request, Payment.Type.ALIPAY);
    }

    private MomiaHttpResponse prepay(HttpServletRequest request, int payType) {
        User user = UserServiceApi.USER.get(request.getParameter("utoken"));
        long orderId = Long.valueOf(request.getParameter("oid"));
        long productId = Long.valueOf(request.getParameter("pid"));
        long skuId = Long.valueOf(request.getParameter("sid"));

        Order order = orderService.get(orderId);
        if (!order.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != productId ||
                order.getSkuId() != skuId) return MomiaHttpResponse.FAILED("订单数据有问题，无效的订单");

        Product product = ProductServiceApi.PRODUCT.get(productId, Product.Type.MINI);
        Sku sku = ProductServiceApi.SKU.get(productId, skuId);
        if (!product.exists() || !sku.exists() || sku.isFinished()) return MomiaHttpResponse.FAILED("活动已结束或下线，不能再付款");

        if (!orderService.prepay(orderId)) return MomiaHttpResponse.FAILED;

        String userCouponIdStr = request.getParameter("coupon");
        long userCouponId = StringUtils.isBlank(userCouponIdStr) ? 0 : Long.valueOf(userCouponIdStr);
        Coupon coupon = useCoupon(user.getId(), order, userCouponId);

        PrepayParam prepayParam = extractPrepayParam(request, order, product, coupon, payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

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

    private PrepayParam extractPrepayParam(HttpServletRequest request, Order order, Product product, Coupon coupon, int payType) {
        PrepayParam prepayParam = new PrepayParam();

        prepayParam.setClientType(extractClientType(request, payType));
        prepayParam.setOrderId(order.getId());
        prepayParam.setProductId(product.getId());
        prepayParam.setProductTitle(product.getTitle());
        prepayParam.setProductUrl(Configuration.getString("Wap.ProductUrl") + "?id=" + product.getId());

        switch (payType) {
            case Payment.Type.ALIPAY:
                prepayParam.setTotalFee(promoServiceFacade.calcTotalFee(order.getTotalFee(), coupon));
                break;
            case Payment.Type.WECHATPAY:
                prepayParam.setTotalFee(new BigDecimal(promoServiceFacade.calcTotalFee(order.getTotalFee(), coupon).multiply(new BigDecimal(100)).intValue()));
                break;
            default: throw new MomiaFailedException("无效的支付类型: " + payType);
        }

        prepayParam.addAll(extractParams(request));
        prepayParam.add("userIp", getRemoteIp(request));

        return prepayParam;
    }

    private int extractClientType(HttpServletRequest request, int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY:
                String type = request.getParameter("type");
                if ("app".equalsIgnoreCase(type)) return ClientType.APP;
                else if ("wap".equalsIgnoreCase(type)) return ClientType.WAP;
                else throw new MomiaFailedException("not supported type: " + type);
            case Payment.Type.WECHATPAY:
                String tradeType = request.getParameter("trade_type");
                if ("APP".equals(tradeType)) return ClientType.APP;
                else if ("JSAPI".equals(tradeType)) return ClientType.WAP;
                else throw new MomiaFailedException("not supported trade type: " + tradeType);
            default: return 0;
        }
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
        Order order = orderService.get(orderId);
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
                !orderService.prepay(orderId) ||
                !orderService.pay(orderId)) return MomiaHttpResponse.FAILED("支付失败");

        ProductServiceApi.PRODUCT.sold(order.getProductId(), order.getCount());
        if (!UserServiceApi.USER.isPayed(order.getCustomerId())) UserServiceApi.USER.setPayed(order.getCustomerId());

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public MomiaHttpResponse checkPayment(@RequestParam String utoken,
                                          @RequestParam(value = "oid") long orderId,
                                          @RequestParam(value = "pid") long productId,
                                          @RequestParam(value = "sid") long skuId) {
        User user = UserServiceApi.USER.get(utoken);
        if (!orderService.check(user.getId(), orderId, productId, skuId)) return MomiaHttpResponse.FAILED("支付失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
