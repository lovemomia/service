package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.PaymentResult;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.exception.MomiaErrorException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.client.ClientType;
import cn.momia.common.deal.gateway.CallbackParam;
import cn.momia.common.deal.gateway.CallbackResult;
import cn.momia.common.deal.gateway.PayType;
import cn.momia.common.deal.gateway.PaymentGateway;
import cn.momia.common.deal.gateway.PrepayParam;
import cn.momia.common.deal.gateway.PrepayResult;
import cn.momia.common.deal.gateway.factory.CallbackParamFactory;
import cn.momia.common.deal.gateway.factory.PaymentGatewayFactory;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.util.RequestUtil;
import cn.momia.service.course.order.OrderPackage;
import cn.momia.api.course.dto.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.coupon.CouponService;
import cn.momia.api.course.dto.UserCoupon;
import cn.momia.service.course.order.Order;
import cn.momia.service.course.order.OrderService;
import cn.momia.service.course.order.Payment;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired private SubjectService subjectService;
    @Autowired private OrderService orderService;
    @Autowired private CouponService couponService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse prepayAlipay(HttpServletRequest request) {
        return prepay(request, PayType.ALIPAY);
    }

    private MomiaHttpResponse prepay(HttpServletRequest request, int payType) {
        User user = userServiceApi.get(request.getParameter("utoken"));
        long orderId = Long.valueOf(request.getParameter("oid"));
        long userCouponId = Long.valueOf(request.getParameter("coupon"));

        Order order = orderService.get(orderId);
        Subject subject = subjectService.get(order.getSubjectId());
        if (!order.exists() || order.getUserId() != user.getId() || !subject.exists()) return MomiaHttpResponse.FAILED("订单数据有问题，无效的订单");

        if (!orderService.prepay(orderId)) return MomiaHttpResponse.FAILED;

        BigDecimal totalFee = order.getTotalFee();
        if (userCouponId > 0) {
            UserCoupon userCoupon = couponService.get(userCouponId);
            if (!userCoupon.exists() || userCoupon.isExpired() || userCoupon.isUsed()) return MomiaHttpResponse.FAILED("无效的红包/优惠券");
            if (!couponService.preUseCoupon(order.getId(), userCouponId)) return MomiaHttpResponse.FAILED;
            totalFee = couponService.calcTotalFee(totalFee, userCoupon);
        }

        PrepayParam prepayParam = buildPrepayParam(request, order, totalFee, subject, payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        if (!prepayResult.isSuccessful()) return MomiaHttpResponse.FAILED;
        return MomiaHttpResponse.SUCCESS(prepayResult);
    }

    private PrepayParam buildPrepayParam(HttpServletRequest request, Order order, BigDecimal totalFee, Subject subject, int payType) {
        PrepayParam prepayParam = new PrepayParam();

        prepayParam.setClientType(extractClientType(request, payType));
        prepayParam.setOrderId(order.getId());
        prepayParam.setProductId(subject.getId());
        prepayParam.setProductTitle(subject.getTitle());
        prepayParam.setProductUrl(Configuration.getString("Wap.ProductUrl") + order.getSubjectId());
        prepayParam.setPaymentResultUrl(Configuration.getString("Wap.PaymentResultUrl") + order.getId());

        switch (payType) {
            case PayType.ALIPAY:
                prepayParam.setTotalFee(totalFee);
                break;
            case PayType.WEIXIN:
                prepayParam.setTotalFee(new BigDecimal(totalFee.multiply(new BigDecimal(100)).intValue()));
                break;
            default: throw new MomiaErrorException("无效的支付类型: " + payType);
        }

        prepayParam.addAll(extractParams(request));
        prepayParam.add("userIp", RequestUtil.getRemoteIp(request));

        return prepayParam;
    }

    private int extractClientType(HttpServletRequest request, int payType) {
        switch (payType) {
            case PayType.ALIPAY:
                String type = request.getParameter("type");
                if ("app".equalsIgnoreCase(type)) return ClientType.APP;
                else if ("wap".equalsIgnoreCase(type)) return ClientType.WAP;
                else throw new MomiaErrorException("not supported type: " + type);
            case PayType.WEIXIN:
                String tradeType = request.getParameter("type");
                if ("APP".equalsIgnoreCase(tradeType)) return ClientType.APP;
                else if ("JSAPI".equalsIgnoreCase(tradeType)) return ClientType.WAP;
                else throw new MomiaErrorException("not supported trade type: " + tradeType);
            default: return 0;
        }
    }

    @RequestMapping(value = "/prepay/weixin", method = RequestMethod.POST)
    public MomiaHttpResponse prepayWeixin(HttpServletRequest request) {
        return prepay(request, PayType.WEIXIN);
    }

    @RequestMapping(value = "/callback/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse alipayCallback(HttpServletRequest request) {
        return callback(request, PayType.ALIPAY);
    }

    private MomiaHttpResponse callback(HttpServletRequest request, int payType) {
        CallbackParam callbackParam = CallbackParamFactory.create(extractParams(request), payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        CallbackResult callbackResult = gateway.callback(callbackParam, new Function<CallbackParam, Boolean>() {
            @Override
            public Boolean apply(CallbackParam callbackParam) {
                return doCallback(callbackParam);
            }
        });

        if (callbackResult.isSuccessful()) return MomiaHttpResponse.SUCCESS("OK");
        return MomiaHttpResponse.SUCCESS("FAIL");
    }

    private boolean doCallback(CallbackParam callbackParam) {
        if (!callbackParam.isPayedSuccessfully()) return true;

        long orderId = callbackParam.getOrderId();
        Order order = orderService.get(orderId);
        if (!order.exists()) {
            // TODO 自动退款
            return true;
        }

        if (order.isPayed()) {
            // TODO 判断是否重复付款，是则退款
            return true;
        }

        boolean isFirstPay = setPayed(order.getUserId());
        Set<Integer> packageTypes = orderService.getOrderPackageTypes(order.getId());

        if (callbackParam.getTotalFee().compareTo(order.getTotalFee()) < 0) {
            UserCoupon userCoupon = couponService.queryByOrder(order.getId());
            if (!userCoupon.exists() ||
                    callbackParam.getTotalFee().compareTo(couponService.calcTotalFee(order.getTotalFee(), userCoupon)) != 0 ||
                    !couponService.useCoupon(order.getId(), userCoupon.getId())) {
                // TODO 自动退款
                LOGGER.error("红包/优惠券不匹配，订单: {}", order.getId());
                return true;
            }

            if (isFirstPay && packageTypes.contains(OrderPackage.Type.PACKAGE)) inviteUserCoupon(order, userCoupon);
        }

        if (isFirstPay && packageTypes.contains(OrderPackage.Type.PACKAGE)) firstPayUserCoupon(order);

        if (!finishPayment(order, createPayment(callbackParam))) return false;

        return true;
    }

    private boolean setPayed(long userId) {
        try {
            return userServiceApi.setPayed(userId);
        } catch (Exception e) {
            LOGGER.error("fail to set payed for user: {}", userId, e);
            return false;
        }
    }

    private void inviteUserCoupon(Order order, UserCoupon userCoupon) {
        try {
            User inviteUser = userServiceApi.getByInviteCode(userCoupon.getInviteCode());
            if (inviteUser.exists() && inviteUser.getId() != order.getUserId()) {
                couponService.distributeInviteUserCoupon(inviteUser.getId(), userCoupon.getCouponId(), null);
            }
        } catch (Exception e) {
            LOGGER.error("返邀请红包失败，订单: {}", order.getId(), e);
        }
    }

    private void firstPayUserCoupon(Order order) {
        try {
            couponService.distributeFirstPayUserCoupon(order.getUserId());
        } catch (Exception e) {
            LOGGER.error("返首次购买红包失败，订单: {}", order.getId(), e);
        }
    }

    private Payment createPayment(CallbackParam callbackParam) {
        Payment payment = new Payment();
        payment.setOrderId(callbackParam.getOrderId());
        payment.setPayer(callbackParam.getPayer());
        payment.setFinishTime(callbackParam.getFinishTime());
        payment.setPayType(callbackParam.getPayType());
        payment.setTradeNo(callbackParam.getTradeNo());
        payment.setFee(callbackParam.getTotalFee());

        return payment;
    }

    private boolean finishPayment(Order order, Payment payment) {
        // TODO 后续的一些操作
        if (orderService.pay(payment)) {
            try {
                userServiceApi.setPayed(order.getUserId());
            } catch (Exception e) {
                LOGGER.error("fail to set payed of user: {}", order.getUserId(), e);
            }

            return true;
        }

        return false;
    }

    @RequestMapping(value = "/callback/weixin", method = RequestMethod.POST)
    public MomiaHttpResponse wechatpayCallback(HttpServletRequest request) {
        return callback(request, PayType.WEIXIN);
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public MomiaHttpResponse check(@RequestParam String utoken, @RequestParam(value = "oid") long orderId) {
        User user = userServiceApi.get(utoken);
        Order order = orderService.get(orderId);

        PaymentResult paymentResult = new PaymentResult();
        if (order.exists() && order.getUserId() == user.getId() && order.isPayed()) {
            paymentResult.setPayed(true);
            paymentResult.setSubjectId(order.getSubjectId());
        } else {
            paymentResult.setPayed(false);
        }

        return MomiaHttpResponse.SUCCESS(paymentResult);
    }
}
