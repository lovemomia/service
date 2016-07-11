package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.activity.Activity;
import cn.momia.api.course.activity.ActivityEntry;
import cn.momia.api.course.expert.Expert;
import cn.momia.api.course.expert.ExpertCourse;
import cn.momia.api.course.expert.ExpertHear;
import cn.momia.api.course.expert.ExpertOrder;
import cn.momia.api.course.expert.ExpertQuestion;
import cn.momia.api.course.expert.HearResult;
import cn.momia.api.course.expert.QExpert;
import cn.momia.api.user.SmsServiceApi;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.platform.Platform;
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
import cn.momia.service.course.activity.Payment;
import cn.momia.service.course.expert.ExpertService;
import cn.momia.service.course.expert.WdPayment;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by hoze on 16/6/16.
 */
@RestController
@RequestMapping(value = "/expert")
public class ExpertController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ExpertController.class);
    private final int status_one = 1;
    private final int status_two = 2;
    private final int status_three = 3;
    private final double hear_fee = 0.1;

    @Autowired private ExpertService expertService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/wd_courses", method = RequestMethod.GET)
    public MomiaHttpResponse courses() {
        return MomiaHttpResponse.SUCCESS(expertService.getExpertCourses());
    }

    @RequestMapping(value = "/wd_questions/{wid}", method = RequestMethod.GET)
    public MomiaHttpResponse questions(@PathVariable(value = "wid") long courseId) {
        return MomiaHttpResponse.SUCCESS(expertService.getExpertQuestions((int)courseId));
    }

    @RequestMapping(value = "/wd_banners", method = RequestMethod.GET)
    public MomiaHttpResponse banners() {
        return MomiaHttpResponse.SUCCESS(expertService.getExpertBanners());
    }

    @RequestMapping(value = "/wd_course/{wid}", method = RequestMethod.GET)
    public MomiaHttpResponse course(@PathVariable(value = "wid") long courseId) {
        return MomiaHttpResponse.SUCCESS(expertService.getExpertCourse((int) courseId));
    }

    @RequestMapping(value = "/coursesPage", method = RequestMethod.GET)
    public MomiaHttpResponse coursesPage(@RequestParam int expertId,@RequestParam int start) {
        return MomiaHttpResponse.SUCCESS(expertService.getExpertCoursesPage(expertId,start));
    }

    @RequestMapping(value = "/questionsPage", method = RequestMethod.GET)
    public MomiaHttpResponse questionPage(@RequestParam int courseId,@RequestParam int start) {
        return MomiaHttpResponse.SUCCESS(expertService.getExpertQuestionsPage(courseId,start));
    }

    @RequestMapping(value = "/questionsExpert", method = RequestMethod.GET)
    public MomiaHttpResponse qExpert(@RequestParam int questionId) {
        QExpert qExpert = new QExpert();
        ExpertQuestion question = expertService.getExpertQuestion(questionId,this.status_one);
        if (!question.exists()){
            question = expertService.getExpertQuestion(questionId,this.status_three);
        }
        qExpert.setQuestion(question);
        qExpert.setExpert(expertService.getExpert((int)question.getExpertId()));
        qExpert.setCourse(expertService.getExpertCourse((int)question.getCourseId()));
        return MomiaHttpResponse.SUCCESS(qExpert);
    }

    @RequestMapping(value = "/questionAnswer", method = RequestMethod.GET)
    public MomiaHttpResponse qAnswer(@RequestParam int questionId, @RequestParam String answer, @RequestParam int mins){
        boolean reData = true;
        log.info(">>>>"+questionId+">>>>"+answer+">>>>"+mins);
        int update_int = expertService.updateQuestionAnswer(questionId, answer, mins);
        if (update_int != 1) reData = false;
        return MomiaHttpResponse.SUCCESS(reData);
    }

    @RequestMapping(value = "/questionJoin", method = RequestMethod.GET)
    public MomiaHttpResponse questionJoin(@RequestParam int courseId, @RequestParam String utoken, @RequestParam String content) {
        ExpertCourse course = expertService.getExpertCourse(courseId);
        User user = userServiceApi.get(utoken);
        long qid = expertService.insertQuestion((int)course.getExpertId(),course.getId(),(int)user.getId(),content);
        long orderId = 0;
        ExpertOrder order = new ExpertOrder();
        if (qid > 0) {
            orderId = expertService.insertOrder((int) qid, course.getId(), (int) user.getId(), 0, this.status_one, course.getPrice(), user.getMobile());
            order = expertService.getExpertOrder((int)orderId,this.status_two);
        }
        return MomiaHttpResponse.SUCCESS(order);
    }

    @RequestMapping(value = "/hearJoin", method = RequestMethod.GET)
    public MomiaHttpResponse hearJoin(@RequestParam int qid, @RequestParam String utoken) {
        HearResult result = new HearResult();
        User user = userServiceApi.get(utoken);
        if (!user.exists()) return MomiaHttpResponse.FAILED("无用户信息");
        ExpertQuestion question = expertService.getExpertQuestion(qid, this.status_one);
        if (!question.exists()){
            question = expertService.getExpertQuestion(qid, this.status_three);
            if (!question.exists())return MomiaHttpResponse.FAILED("无提问信息");
        }
        ExpertCourse course = expertService.getExpertCourse((int)question.getCourseId());
        Expert expert = expertService.getExpert((int)question.getExpertId());
        if (user.getId() == question.getUserId() || user.getId() == expert.getUserId()){//验证用户如果是提问或回答问题者都不需要付费
            log.info("hearJoin>>>>>验证用户如果是提问或回答问题者都不需要付费");
            log.info("========>"+ question.getAnswer());
            result.setQuestion(question);
            ExpertQuestion answer = result.getQuestion();
            log.info("========123123>"+ answer.getAnswer());
            result.setStatus(this.status_two);
        }else{
            ExpertHear hear = expertService.getExpertHear(qid,(int)user.getId());
            if (!hear.exists()){
                log.info("hearJoin>>>>>验证用户没有产生订单，进行订单创建");
                long hid = expertService.insertHear(qid,(int)user.getId());
                long orderId = expertService.insertOrder(question.getId(), course.getId(), (int) user.getId(), (int)hid, this.status_two, course.getPrice(), user.getMobile());
                ExpertOrder order = expertService.getExpertOrder((int)orderId,this.status_two);
                order.setIsUse(isUse((int)user.getId(), order.getId()));
                result.setOrder(order);
                result.setStatus(this.status_one);
            }else{
                log.info("hearJoin>>>> hear id >>>>>"+hear.getId()+"===="+hear.getUserId());
                if (hear.getStatus() == this.status_one){
                    log.info("hearJoin>>>>>验证用户已有订单并且订单为已付款");
                    result.setQuestion(question);
                    result.setStatus(this.status_two);
                }else{
                    log.info("hearJoin>>>>>验证用户已有订单但订单为未付款");
                    ExpertOrder order = expertService.getExpertOrderById((int)user.getId(), (int)question.getCourseId(), question.getId(), hear.getId(), this.status_two);
                    order.setIsUse(isUse((int)user.getId(), order.getId()));
                    result.setOrder(order);
                    result.setStatus(this.status_one);
                }
            }
        }

        return MomiaHttpResponse.SUCCESS(result);
    }

    private boolean isUse(int userId, int orderId){
        return expertService.isUserAsset(userId, orderId);
    }

    @RequestMapping(value = "/payment/prepay/asset", method = RequestMethod.POST)
    public MomiaHttpResponse prepayasset(HttpServletRequest request) {
        boolean reData = true;
        int orderId = Integer.valueOf(request.getParameter("oid"));
        ExpertOrder order = expertService.getExpertOrder(orderId,this.status_two);
        if (!order.exists()) return MomiaHttpResponse.FAILED("您还没有订单");
        BigDecimal totalFee = new BigDecimal(hear_fee);
        if (order.getType() == 1){
            totalFee = order.getPrice();
        }
        String fee = new DecimalFormat("#.00").format(totalFee);
        WdPayment payment = new WdPayment();
        payment.setOrderId(order.getId());
        payment.setPayer(""+order.getUserId());
        payment.setFinishTime(new Date());
        payment.setPayType(99);
        payment.setTradeNo(""+order.getId());
        payment.setFee(new BigDecimal(fee));

        expertService.updateUserAsset((int)order.getUserId(),totalFee,2);

        if (!finishPayment(payment)) {
            reData = false;
        }

        return MomiaHttpResponse.SUCCESS(reData);
    }

    @RequestMapping(value = "/payment/prepay/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse prepayAlipay(HttpServletRequest request) {
        return prepay(request, PayType.ALIPAY);
    }

    private MomiaHttpResponse prepay(HttpServletRequest request, int payType) {
        int orderId = Integer.valueOf(request.getParameter("oid"));
        ExpertOrder order = expertService.getExpertOrder(orderId,this.status_two);
        if (!order.exists()) return MomiaHttpResponse.FAILED("无效的订单");

        ExpertQuestion question = new ExpertQuestion();

        if (order.getType() == 2) {
            question = expertService.getExpertQuestion((int) order.getQuestionId(), this.status_one);//偷听 状态为1时无答案
            if (!question.exists()){
                question = expertService.getExpertQuestion((int) order.getQuestionId(), this.status_three);//偷听 状态为3时有答案
            }
        }else{
            question = expertService.getExpertQuestion((int) order.getQuestionId(), this.status_two);
        }
        if (!question.exists()) return MomiaHttpResponse.FAILED("无效的问题");

//        if (!activityService.prepay(entryId)) return MomiaHttpResponse.FAILED;

        PrepayParam prepayParam = buildPrepayParam(request, question, order, payType);
        PaymentGateway gateway = PaymentGatewayFactory.create(payType);
        PrepayResult prepayResult = gateway.prepay(prepayParam);

        if (!prepayResult.isSuccessful()) return MomiaHttpResponse.FAILED;
        return MomiaHttpResponse.SUCCESS(prepayResult);
    }

    private PrepayParam buildPrepayParam(HttpServletRequest request, ExpertQuestion question, ExpertOrder order, int payType) {
        PrepayParam prepayParam = new PrepayParam();
        String ptStr = "专家答案旁听"+question.getCourseId()+"－"+question.getId();
        BigDecimal totalFee = new BigDecimal(hear_fee);
        if (order.getType() == 1){
            ptStr = "对专家提问"+question.getCourseId()+"－"+question.getId();
            totalFee = order.getPrice();
        }
        prepayParam.setPlatform(extractClientType(request, payType));
        prepayParam.setOutTradeNo("exp" + order.getId());
        prepayParam.setProductId(question.getId());
        prepayParam.setProductTitle(ptStr);
        prepayParam.setProductUrl("http://m.momia.cn");
        prepayParam.setPaymentResultUrl("http://m.momia.cn");

        switch (payType) {
            case PayType.ALIPAY:
                String fee = new DecimalFormat("#.00").format(totalFee);
                prepayParam.setTotalFee(new BigDecimal(fee));
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
                if ("app".equalsIgnoreCase(type)) return Platform.APP;
                else if ("wap".equalsIgnoreCase(type)) return Platform.WAP;
                else throw new MomiaErrorException("not supported type: " + type);
            case PayType.WEIXIN:
                String tradeType = request.getParameter("type");
                if ("APP".equalsIgnoreCase(tradeType)) return Platform.APP;
                else if ("JSAPI".equalsIgnoreCase(tradeType)) return Platform.WAP;
                else throw new MomiaErrorException("not supported trade type: " + tradeType);
            default: return 0;
        }
    }

    @RequestMapping(value = "/payment/prepay/weixin", method = RequestMethod.POST)
    public MomiaHttpResponse prepayWeixin(HttpServletRequest request) {
        return prepay(request, PayType.WEIXIN);
    }

    @RequestMapping(value = "/payment/callback/alipay", method = RequestMethod.POST)
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

        log.info("支付id>>>>>1>=" + callbackParam.getOrderId());
        long orderId = callbackParam.getOrderId();
        ExpertOrder expertOrder = expertService.getExpertOrder((int) orderId,this.status_two);
        if (!expertOrder.exists()) {
            // TODO 自动退款
            return true;
        }

        if (expertOrder.isPayed()) {
            // TODO 判断是否重复付款，是则退款
            return true;
        }

        if (!finishPayment(createPayment(callbackParam))) return false;

        return true;
    }

    private boolean finishPayment(WdPayment payment) {
        return expertService.pay(payment);
    }

    private WdPayment createPayment(CallbackParam callbackParam) {
        WdPayment payment = new WdPayment();
        payment.setOrderId(callbackParam.getOrderId());
        payment.setPayer(callbackParam.getPayer());
        payment.setFinishTime(callbackParam.getFinishTime());
        payment.setPayType(callbackParam.getPayType());
        payment.setTradeNo(callbackParam.getTradeNo());
        payment.setFee(callbackParam.getTotalFee());

        return payment;
    }

    @RequestMapping(value = "/payment/callback/weixin", method = RequestMethod.POST)
    public MomiaHttpResponse wechatpayCallback(HttpServletRequest request) {
        return callback(request, PayType.WEIXIN);
    }

    @RequestMapping(value = "/payment/check", method = RequestMethod.POST)
    public MomiaHttpResponse check(@RequestParam(value = "oid") long orderId) {
        ExpertOrder order = expertService.getExpertOrder((int)orderId,3);
        return MomiaHttpResponse.SUCCESS(order.exists() && order.isPayed());
    }

    @RequestMapping(value = "/myQuestionPages", method = RequestMethod.GET)
    public MomiaHttpResponse QuestionPages(@RequestParam String utoken, @RequestParam int type, @RequestParam int start){
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(expertService.getMyQuestionsPage((int) user.getId(), type, start));
    }

    @RequestMapping(value = "/myAnswerNullPages", method = RequestMethod.GET)
    public MomiaHttpResponse answerNullPages(@RequestParam String utoken, @RequestParam int start){
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(expertService.getMyAnswerNullPage((int) user.getId(), start));
    }

    @RequestMapping(value = "/myUserAsset", method = RequestMethod.GET)
    public MomiaHttpResponse userAsset(@RequestParam String utoken){
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(expertService.getUserAsset((int)user.getId()));
    }

    @RequestMapping(value = "/my_centre", method = RequestMethod.GET)
    public MomiaHttpResponse myCentre(@RequestParam String utoken){
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(expertService.getMyCentre((int)user.getId()));
    }

    @RequestMapping(value = "/course_count", method = RequestMethod.GET)
    public MomiaHttpResponse myCourseCount(@RequestParam int wid){
        boolean reData = true;
        log.info("tj-count>>>>>"+wid);
        int update_int = expertService.updateCourseCount(wid);
        if (update_int != 1) reData = false;

        log.info("reData >>>>>"+reData);

        return MomiaHttpResponse.SUCCESS(reData);
    }

    private BigDecimal getBigDecimalBy2(BigDecimal number){
        return number.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
