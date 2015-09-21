package cn.momia.service.deal.web.ctrl;

import cn.momia.api.base.BaseServiceApi;
import cn.momia.api.user.User;
import cn.momia.api.user.UserServiceApi;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.deal.gateway.CallbackParam;
import cn.momia.service.deal.gateway.CallbackResult;
import cn.momia.service.deal.gateway.ClientType;
import cn.momia.service.deal.gateway.PaymentGateway;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.PrepayResult;
import cn.momia.service.deal.gateway.factory.CallbackParamFactory;
import cn.momia.service.deal.gateway.factory.PaymentGatewayFactory;
import cn.momia.service.order.product.Order;
import cn.momia.service.order.product.OrderService;
import cn.momia.service.order.product.Payment;
import cn.momia.api.product.ProductServiceApi;
import cn.momia.api.product.Product;
import cn.momia.api.product.sku.Sku;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.promo.facade.PromoServiceFacade;
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
import java.util.Date;

@RestController
public class PaymentController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired private OrderService orderService;
    @Autowired private PromoServiceFacade promoServiceFacade;

    @RequestMapping(value = "/payment/prepay/alipay", method = RequestMethod.POST)
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
        Coupon coupon = preUseCoupon(user.getId(), order, userCouponId);

        PrepayParam prepayParam = extractPrepayParam(request, order, product, coupon, payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        if (!prepayResult.isSuccessful()) return MomiaHttpResponse.FAILED;
        return MomiaHttpResponse.SUCCESS(prepayResult);
    }

    private Coupon preUseCoupon(long userId, Order order, long userCouponId) {
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

    @RequestMapping(value = "/payment/prepay/wechatpay", method = RequestMethod.POST)
    public MomiaHttpResponse prepayWechatpay(HttpServletRequest request) {
        return prepay(request, Payment.Type.WECHATPAY);
    }

    @RequestMapping(value = "/payment/prepay/free", method = RequestMethod.POST)
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
            Coupon coupon = preUseCoupon(user.getId(), order, userCouponId);
            totalFee = promoServiceFacade.calcTotalFee(totalFee, coupon);
        }

        if (totalFee.compareTo(new BigDecimal(0)) != 0 ||
                !orderService.prepay(orderId) ||
                !finishPayment(order, createPayment(order))) return MomiaHttpResponse.FAILED("支付失败");

        return MomiaHttpResponse.SUCCESS;
    }

    private Payment createPayment(Order order) {
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPayer(String.valueOf(order.getCustomerId()));
        payment.setFinishTime(new Date());
        payment.setPayType(Payment.Type.FREEPAY);
        payment.setTradeNo("");
        payment.setFee(new BigDecimal(0));

        return payment;
    }

    private boolean finishPayment(Order order, Payment payment) {
        if (!orderService.pay(payment)) return false;

        updateUserCoupon(order);
        updateSales(order);
        notifyUser(order);

        if (!UserServiceApi.USER.isPayed(order.getCustomerId())) {
            if (UserServiceApi.USER.setPayed(order.getCustomerId())) distributeCoupon(order);
        }

        return true;
    }

    private void updateUserCoupon(Order order) {
        try {
            UserCoupon userCoupon = promoServiceFacade.getNotUsedUserCouponByOrder(order.getId());
            if (!promoServiceFacade.useUserCoupon(order.getCustomerId(), order.getId(), userCoupon.getId()))
                LOGGER.error("fail to update user coupon of order: {}", order.getId());
        } catch (Exception e) {
            LOGGER.error("fail to update user coupon of order: {}", order.getId(), e);
        }
    }

    private void updateSales(Order order) {
        try {
            ProductServiceApi.PRODUCT.sold(order.getProductId(), order.getCount());
        } catch (Exception e) {
            LOGGER.error("fail to update sales of order: {}", order.getId(), e);
        }
    }

    private void notifyUser(Order order) {
        try {
            Product product = ProductServiceApi.PRODUCT.get(order.getProductId(), Product.Type.BASE_WITH_SKU);
            Sku sku = product.getSku(order.getSkuId());
            if (!sku.exists()) return;

            StringBuilder msg = new StringBuilder();
            msg.append("您的订单：\"")
                    .append(product.getTitle())
                    .append("\"付款成功");

            if (sku.getType() == 1) {
                msg.append("，参加规则详见活动说明");
            } else {
                msg.append("，时间：")
                        .append(sku.getTime())
                        .append("，地点：");
                String address = sku.getAddress();
                if (StringUtils.isBlank(address)) address = product.getAddress();
                msg.append(address);
                msg.append("，券号：")
                        .append(order.getTicketNumber());
            }

            msg.append("，请添加客服微信dorakids01【松果亲子】");
            BaseServiceApi.SMS.notify(order.getMobile(), msg.toString());
        } catch (Exception e) {
            LOGGER.error("fail to notify user for order: {}", order.getId(), e);
        }
    }

    private void distributeCoupon(Order order) {
        try {
            if (StringUtils.isBlank(order.getInviteCode())) return;
            long userId = UserServiceApi.USER.getIdByInviteCode(order.getInviteCode());
            if (userId <= 0 || userId == order.getCustomerId()) return;

            promoServiceFacade.distributeShareCoupon(order.getCustomerId(), userId, order.getTotalFee());
        } catch (Exception e) {
            LOGGER.error("fail to distribute share coupon for order: {}", order.getId(), e);
        }
    }

    @RequestMapping(value = "/payment/check", method = RequestMethod.GET)
    public MomiaHttpResponse checkPayment(@RequestParam String utoken,
                                          @RequestParam(value = "oid") long orderId,
                                          @RequestParam(value = "pid") long productId,
                                          @RequestParam(value = "sid") long skuId) {
        User user = UserServiceApi.USER.get(utoken);
        if (!orderService.check(user.getId(), orderId, productId, skuId)) return MomiaHttpResponse.FAILED("支付失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/callback/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse alipayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.ALIPAY);
    }

    private MomiaHttpResponse callback(HttpServletRequest request, int payType) {
        CallbackParam callbackParam = CallbackParamFactory.create(extractParams(request), payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        CallbackResult result = gateway.callback(callbackParam);

        if (!result.isSuccessful()) return MomiaHttpResponse.SUCCESS("OK");

        long orderId = result.getOrderId();
        Order order = orderService.get(orderId);
        if (!order.exists()) {
            // TODO 自动退款
            return MomiaHttpResponse.SUCCESS("OK");
        }

        if (order.isPayed()) {
            // TODO 判断是否重复付款，是则退款
            return MomiaHttpResponse.SUCCESS("OK");
        }

        if (!finishPayment(order, createPayment(result))) return MomiaHttpResponse.SUCCESS("FAIL");

        return MomiaHttpResponse.SUCCESS("OK");
    }

    private Payment createPayment(CallbackResult result) {
        Payment payment = new Payment();
        payment.setOrderId(result.getOrderId());
        payment.setPayer(result.getPayer());
        payment.setFinishTime(result.getFinishTime());
        payment.setPayType(result.getPayType());
        payment.setTradeNo(result.getTradeNo());
        payment.setFee(result.getTotalFee());

        return payment;
    }

    @RequestMapping(value = "/callback/wechatpay", method = RequestMethod.POST)
    public MomiaHttpResponse wechatpayCallback(HttpServletRequest request) {
        return callback(request, Payment.Type.WECHATPAY);
    }
}
