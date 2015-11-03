package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.PaymentDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.client.ClientType;
import cn.momia.common.deal.gateway.CallbackParam;
import cn.momia.common.deal.gateway.PayType;
import cn.momia.common.deal.gateway.PaymentGateway;
import cn.momia.common.deal.gateway.PrepayParam;
import cn.momia.common.deal.gateway.PrepayResult;
import cn.momia.common.deal.gateway.factory.CallbackParamFactory;
import cn.momia.common.deal.gateway.factory.PaymentGatewayFactory;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.util.RequestUtil;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderService;
import cn.momia.service.course.subject.order.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired private SubjectService subjectService;
    @Autowired private OrderService orderService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse prepayAlipay(HttpServletRequest request) {
        return prepay(request, PayType.ALIPAY);
    }

    private MomiaHttpResponse prepay(HttpServletRequest request, int payType) {
        UserDto user = userServiceApi.get(request.getParameter("utoken"));
        long orderId = Long.valueOf(request.getParameter("oid"));

        Order order = orderService.get(orderId);
        Subject subject = subjectService.get(order.getSubjectId());
        if (!order.exists() || order.getUserId() != user.getId() || !subject.exists()) return MomiaHttpResponse.FAILED("订单数据有问题，无效的订单");

        if (!orderService.prepay(orderId)) return MomiaHttpResponse.FAILED;

        PrepayParam prepayParam = extractPrepayParam(request, order, subject, payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        if (!prepayResult.isSuccessful()) return MomiaHttpResponse.FAILED;
        return MomiaHttpResponse.SUCCESS(prepayResult);
    }

    private PrepayParam extractPrepayParam(HttpServletRequest request, Order order, Subject subject, int payType) {
        PrepayParam prepayParam = new PrepayParam();

        prepayParam.setClientType(extractClientType(request, payType));
        prepayParam.setOrderId(order.getId());
        prepayParam.setProductId(subject.getId());
        prepayParam.setProductTitle(subject.getTitle());
        prepayParam.setProductUrl(Configuration.getString("Wap.ProductUrl") + order.getSubjectId());
        prepayParam.setPaymentResultUrl(Configuration.getString("Wap.PaymentResultUrl") + order.getId());

        switch (payType) {
            case PayType.ALIPAY:
                prepayParam.setTotalFee(order.getTotalFee());
                break;
            case PayType.WEIXIN:
                prepayParam.setTotalFee(new BigDecimal(order.getTotalFee().multiply(new BigDecimal(100)).intValue()));
                break;
            default: throw new MomiaFailedException("无效的支付类型: " + payType);
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
                else throw new MomiaFailedException("not supported type: " + type);
            case PayType.WEIXIN:
                String tradeType = request.getParameter("type");
                if ("APP".equalsIgnoreCase(tradeType)) return ClientType.APP;
                else if ("JSAPI".equalsIgnoreCase(tradeType)) return ClientType.WAP;
                else throw new MomiaFailedException("not supported trade type: " + tradeType);
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

        if (!callbackParam.isPayedSuccessfully()) return MomiaHttpResponse.SUCCESS("OK");

        long orderId = callbackParam.getOrderId();
        Order order = orderService.get(orderId);
        if (!order.exists()) {
            // TODO 自动退款
            return MomiaHttpResponse.SUCCESS("OK");
        }

        if (order.isPayed()) {
            // TODO 判断是否重复付款，是则退款
            return MomiaHttpResponse.SUCCESS("OK");
        }

        if (!finishPayment(order, createPayment(callbackParam))) return MomiaHttpResponse.SUCCESS("FAIL");

        return MomiaHttpResponse.SUCCESS("OK");
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
                userServiceApi.payed(order.getUserId());
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
        UserDto user = userServiceApi.get(utoken);
        Order order = orderService.get(orderId);

        PaymentDto paymentDto = new PaymentDto();
        if (order.exists() && order.getUserId() == user.getId() && order.isPayed()) {
            paymentDto.setPayed(true);
            paymentDto.setSubjectId(order.getSubjectId());
        } else {
            paymentDto.setPayed(false);
        }

        return MomiaHttpResponse.SUCCESS(paymentDto);
    }
}
