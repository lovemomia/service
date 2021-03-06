package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.course.Course;
import cn.momia.api.course.dto.subject.SubjectOrder;
import cn.momia.api.course.dto.subject.SubjectPackage;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.dto.PagedList;
import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.common.deal.gateway.PayType;
import cn.momia.common.deal.gateway.PaymentGateway;
import cn.momia.common.deal.gateway.RefundParam;
import cn.momia.common.deal.gateway.RefundQueryParam;
import cn.momia.common.deal.gateway.factory.PaymentGatewayFactory;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.CourseService;
import cn.momia.api.course.dto.coupon.UserCoupon;
import cn.momia.api.course.dto.subject.Subject;
import cn.momia.api.course.dto.coupon.CouponCode;
import cn.momia.service.course.order.Payment;
import cn.momia.service.course.order.Refund;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.subject.SubjectSku;
import cn.momia.service.course.coupon.CouponService;
import cn.momia.service.course.order.Order;
import cn.momia.service.course.order.OrderService;
import cn.momia.service.course.order.OrderPackage;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/order")
public class OrderController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private OrderService orderService;
    @Autowired private CouponService couponService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse placeOrder(@RequestBody Order order) {
        List<OrderPackage> orderPackages = order.getPackages();

        Set<Long> skuIds = new HashSet<Long>();
        for (OrderPackage orderPackage : orderPackages) {
            skuIds.add(orderPackage.getSkuId());
        }
        List<SubjectSku> skus = subjectService.listSkus(skuIds);

        if (!checkAndCompleteOrder(order, skus)) return MomiaHttpResponse.FAILED("无效的订单数据");

        boolean isTrial = subjectService.isTrial(order.getSubjectId());
        if (isTrial && !subjectService.decreaseStock(order.getSubjectId(), order.getCount())) return MomiaHttpResponse.FAILED("下单失败，库存不足或已售完");

        long orderId = 0;
        try {
            orderId = orderService.add(order);
            order.setId(orderId);
            return MomiaHttpResponse.SUCCESS(buildMiniSubjectOrder(order));
        } catch (Exception e) {
            return MomiaHttpResponse.FAILED("下单失败");
        } finally {
            if (orderId <= 0 && isTrial) subjectService.increaseStock(order.getSubjectId(), order.getCount());
        }
    }

    private boolean checkAndCompleteOrder(Order order, List<SubjectSku> skus) {
        if (order.isInvalid()) return false;

        User user = userServiceApi.get(order.getUserId());
        if (!user.exists()) throw new MomiaErrorException("用户不存在");
        if (subjectService.isTrial(order.getSubjectId()) && (user.isPayed() || orderService.hasTrialOrder(user.getId()))) throw new MomiaErrorException("本课程包只供新用户专享");

        Map<Long, SubjectSku> skusMap = new HashMap<Long, SubjectSku>();
        for (SubjectSku sku : skus) {
            if (sku.getSubjectId() != order.getSubjectId()) return false;
            skusMap.put(sku.getId(), sku);
        }

        String code = order.getCouponCode();
        order.setCouponCode("");
        CouponCode couponCode = couponService.getCouponCode(code);
        boolean couponCodeUsed = !couponCode.exists();
        List<OrderPackage> orderPackages = order.getPackages();
        for (OrderPackage orderPackage : orderPackages) {
            SubjectSku sku = skusMap.get(orderPackage.getSkuId());
            if (sku == null) return false;

            if (sku.getLimit() > 0) checkLimit(order.getUserId(), sku.getId(), sku.getLimit());

            BigDecimal skuPrice = sku.getPrice();
            if (!couponCodeUsed && skuPrice.compareTo(couponCode.getConsumption()) >= 0) {
                couponCodeUsed = true;
                order.setCouponCode(code);
                skuPrice = skuPrice.subtract(couponCode.getDiscount());
            }
            orderPackage.setPrice(skuPrice);
            orderPackage.setBookableCount(sku.getCourseCount());
            orderPackage.setTime(sku.getTime());
            orderPackage.setTimeUnit(sku.getTimeUnit());
        }

        return true;
    }

    private void checkLimit(long userId, long skuId, int limit) {
        int boughtCount = orderService.getBoughtCount(userId, skuId);
        if (boughtCount > limit) throw new MomiaErrorException("超出购买限额");
    }

    private SubjectOrder buildMiniSubjectOrder(Order order) {
        SubjectOrder subjectOrder = new SubjectOrder();
        subjectOrder.setId(order.getId());
        subjectOrder.setSubjectId(order.getSubjectId());
        subjectOrder.setCount(order.getCount());
        subjectOrder.setTotalFee(order.getTotalFee());
        subjectOrder.setStatus(order.getStatus() <= Order.Status.PRE_PAYED ? Order.Status.PRE_PAYED : order.getStatus());
        subjectOrder.setAddTime(order.getAddTime());

        return subjectOrder;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam String utoken, @RequestParam(value = "oid") long orderId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(orderService.delete(user.getId(), orderId));
    }

    @RequestMapping(value = "/{oid}/refund", method = RequestMethod.POST)
    public MomiaHttpResponse refund(@RequestParam String utoken,
                                    @PathVariable(value = "oid") long orderId,
                                    @RequestParam BigDecimal fee,
                                    @RequestParam String message) {
        if (courseService.queryBookedCourseCounts(Sets.newHashSet(orderId)).get(orderId) > 0) return MomiaHttpResponse.FAILED("已经选过课的订单不能申请退款");

        Payment payment = orderService.getPayment(orderId);
        if (!payment.exists()) return MomiaHttpResponse.FAILED("未付款的订单不能退款");
        if (payment.getFee().compareTo(fee) < 0) return MomiaHttpResponse.FAILED("退款金额超过了付款金额");

        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(orderService.applyRefund(user.getId(), fee, message, payment));
    }

    @RequestMapping(value = "/{oid}/refund/check", method = RequestMethod.POST)
    public MomiaHttpResponse refundCheck(@PathVariable(value = "oid") long orderId) {
        Order order = orderService.get(orderId);
        if (order.getStatus() != Order.Status.TO_REFUND) return MomiaHttpResponse.FAILED("该订单并未申请退款");

        Payment payment = orderService.getPayment(orderId);
        if (!payment.exists()) return MomiaHttpResponse.FAILED("未付款的订单不能退款");

        Refund refund = orderService.queryRefund(orderId, payment.getId());
        if (!refund.exists()) return MomiaHttpResponse.FAILED("无效的退款申请");

        if (courseService.queryBookedCourseCounts(Sets.newHashSet(orderId)).get(orderId) > 0) return MomiaHttpResponse.FAILED("已经选过课的订单不能退款");

        RefundParam refundParam = new RefundParam();
        refundParam.setPayType(payment.getPayType());
        refundParam.setRefundId(refund.getId());
        refundParam.setRefundTime(new Date());
        refundParam.setTradeNo(payment.getTradeNo());
        switch (payment.getPayType()) {
            case PayType.ALIPAY:
                refundParam.setTotalFee(payment.getFee());
                refundParam.setRefundFee(refund.getRefundFee());
                break;
            case PayType.WEIXIN:
            case PayType.WEIXIN_APP:
            case PayType.WEIXIN_JSAPI:
                refundParam.setTotalFee(new BigDecimal(payment.getFee().multiply(new BigDecimal(100)).intValue()));
                refundParam.setRefundFee(new BigDecimal(refund.getRefundFee().multiply(new BigDecimal(100)).intValue()));
                break;
            default: throw new MomiaErrorException("无效的支付类型: " + payment.getPayType());
        }
        refundParam.setRefundMessage(order.getRefundMessage());
        PaymentGateway gateway = PaymentGatewayFactory.create(payment.getPayType());
        if (gateway.refund(refundParam)) orderService.refundChecked(orderId);

        return MomiaHttpResponse.SUCCESS(true);
    }

    @RequestMapping(value = "/{oid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken, @PathVariable(value = "oid") long orderId) {
        User user = userServiceApi.get(utoken);
        Order order = orderService.get(orderId);
        if (!user.exists() || !order.exists() || order.getUserId() != user.getId()) return MomiaHttpResponse.FAILED("无效的订单");

        String title;
        String cover;
        List<Long> courseIds = order.getCourseIds();
        if (courseIds.size() >= 1) {
            Course course = courseService.get(courseIds.get(0));
            title = course.getTitle();
            cover = course.getCover();
        } else {
            List<OrderPackage> orderPackages = order.getPackages();
            if (orderPackages.isEmpty()) throw new MomiaErrorException("无效的订单");

            if (orderPackages.size() == 1) {
                OrderPackage orderPackage = orderPackages.get(0);
                title = orderPackage.getTitle();
                cover = orderPackage.getCover();
            } else {
                title = Configuration.getString("Package.MultiTitle");
                cover = Configuration.getString("Package.MultiCover");
            }
        }
        Map<Long, Integer> finishedCourceCounts = courseService.queryFinishedCourseCounts(Sets.newHashSet(orderId));
        SubjectOrder detailOrder = buildFullSubjectOrder(order, title, cover, finishedCourceCounts.get(orderId), courseIds);

        if (order.getStatus() == Order.Status.REFUND_CHECKED) {
            Payment payment = orderService.getPayment(orderId);
            Refund refund = orderService.queryRefund(orderId, payment.getId());
            if (payment.exists() && PayType.isWeixinPay(payment.getPayType()) && refund.exists()) queryRefund(detailOrder, payment, refund);
        }

        return MomiaHttpResponse.SUCCESS(detailOrder);
    }

    private SubjectOrder buildFullSubjectOrder(Order order, String title, String cover, int finishedCourseCount, List<Long> courseIds) {
        SubjectOrder subjectOrder = buildBaseSubjectOrder(order, title, cover, finishedCourseCount);
        if (courseIds.size() >= 1) subjectOrder.setCourseId(courseIds.get(0));

        if (order.isPayed()) {
            UserCoupon userCoupon = couponService.queryUsedByOrder(order.getId());
            if (userCoupon.exists()) {
                subjectOrder.setUserCouponId(userCoupon.getId());
                subjectOrder.setCouponId(userCoupon.getCouponId());
                subjectOrder.setCouponType(userCoupon.getType());
                subjectOrder.setDiscount(userCoupon.getDiscount());
                subjectOrder.setCouponDesc(userCoupon.getDiscount() + "元红包"); // TODO 更多类型
            }
        }

        return subjectOrder;
    }

    private SubjectOrder buildBaseSubjectOrder(Order order, String title, String cover, int finishedCourseCount) {
        SubjectOrder subjectOrder = buildMiniSubjectOrder(order);
        int bookableCourseCount = order.getBookableCourseCount();
        if (bookableCourseCount > 0) {
            subjectOrder.setBookingStatus(1);
        } else {
            int totalCourseCount = order.getTotalCourseCount();
            if (finishedCourseCount < totalCourseCount) subjectOrder.setBookingStatus(2);
            else subjectOrder.setBookingStatus(3);
        }
        subjectOrder.setTitle(title);
        subjectOrder.setCover(cover);

        subjectOrder.setCanRefund(!order.isCanceled() && courseService.queryBookedCourseCounts(Sets.newHashSet(order.getId())).get(order.getId()) <= 0);

        if (order.isPayed()) {
            Payment payment = orderService.getPayment(order.getId());
            if (payment.exists()) {
                subjectOrder.setPayedFee(payment.getFee());
                int payType = payment.getPayType();
                payType = (payType == PayType.WEIXIN_APP || payType == PayType.WEIXIN_JSAPI) ? PayType.WEIXIN : payType;
                subjectOrder.setPayType(payType);
            }
        }

        return subjectOrder;
    }

    private void queryRefund(SubjectOrder subjectOrder, Payment payment, Refund refund) {
        try {
            RefundQueryParam refundQueryParam = new RefundQueryParam();
            refundQueryParam.setPayType(payment.getPayType());
            refundQueryParam.setTradeNo(payment.getTradeNo());
            PaymentGateway gateway = PaymentGatewayFactory.create(payment.getPayType());
            if (gateway.refundQuery(refundQueryParam)) {
                orderService.finishRefund(refund);
                subjectOrder.setStatus(Order.Status.REFUNDED);
            }
        } catch (Exception e) {
            LOGGER.error("fail to query refund for order: {}", subjectOrder.getId(), e);
        }
    }

    @RequestMapping(value = "/bookable", method = RequestMethod.GET)
    public MomiaHttpResponse listBookableOrders(@RequestParam String utoken,
                                                @RequestParam(value = "oid") long orderId,
                                                @RequestParam int start,
                                                @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        User user = userServiceApi.get(utoken);

        long totalCount = orderId > 0 ? orderService.queryBookableCountByUserAndOrder(user.getId(), orderId) : orderService.queryBookableCountByUser(user.getId());
        List<OrderPackage> orderPackages = orderId > 0 ? orderService.queryBookableByUserAndOrder(user.getId(), orderId, start, count) : orderService.queryBookableByUser(user.getId(), start, count);

        PagedList<SubjectPackage> pagedSubjectPackages = buildPagedSubjectPackages(orderPackages, totalCount, start, count);

        return MomiaHttpResponse.SUCCESS(pagedSubjectPackages);
    }

    private PagedList<SubjectPackage> buildPagedSubjectPackages(List<OrderPackage> orderPackages, long totalCount, int start, int count) {
        Set<Long> packageIds = new HashSet<Long>();
        Set<Long> orderIds = new HashSet<Long>();
        Set<Long> courseIds = new HashSet<Long>();
        for (OrderPackage orderPackage : orderPackages) {
            packageIds.add(orderPackage.getId());
            orderIds.add(orderPackage.getOrderId());
            if (orderPackage.getCourseId() > 0) courseIds.add(orderPackage.getCourseId());
        }

        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<Order> orders = orderService.list(orderIds);
        Set<Long> subjectIds = new HashSet<Long>();
        Map<Long, Order> ordersMap = new HashMap<Long, Order>();
        for (Order order : orders) {
            subjectIds.add(order.getSubjectId());
            ordersMap.put(order.getId(), order);
        }

        List<Subject> subjects = subjectService.list(subjectIds);
        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        List<SubjectPackage> subjectPackages = new ArrayList<SubjectPackage>();
        for (OrderPackage orderPackage : orderPackages) {
            Order order = ordersMap.get(orderPackage.getOrderId());
            if (order == null) continue;
            Subject subject = subjectsMap.get(order.getSubjectId());
            if (subject == null) continue;
            SubjectSku sku = subject.getSku(orderPackage.getSkuId());
            if (!sku.exists()) continue;

            SubjectPackage subjectPackage = new SubjectPackage();
            subjectPackage.setPackageId(orderPackage.getId());
            subjectPackage.setSubjectId(order.getSubjectId());
            subjectPackage.setTitle(orderPackage.getTitle());
            subjectPackage.setCover(orderPackage.getCover());
            subjectPackage.setBookableCourseCount(orderPackage.getBookableCount());

            subjectPackage.setExpireTime("购买日期: " + TimeUtil.SHORT_DATE_FORMAT.format(order.getAddTime()));

            subjectPackage.setCourseId(orderPackage.getCourseId());
            Course course = coursesMap.get(orderPackage.getCourseId());
            if (course != null) {
                subjectPackage.setCover(course.getCover());
                subjectPackage.setTitle(course.getTitle());
            }

            subjectPackages.add(subjectPackage);
        }

        PagedList<SubjectPackage> pagedSubjectPackages = new PagedList<SubjectPackage>(totalCount, start, count);
        pagedSubjectPackages.setList(subjectPackages);

        return pagedSubjectPackages;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse listOrders(@RequestParam String utoken,
                                        @RequestParam int status,
                                        @RequestParam int start,
                                        @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        User user = userServiceApi.get(utoken);

        long totalCount = orderService.queryCountByUser(user.getId(), status);
        List<Order> orders = orderService.queryByUser(user.getId(), status, start, count);

        PagedList<SubjectOrder> pagedSubjectOrders = buildPagedSubjectOrders(orders, totalCount, start, count);

        return MomiaHttpResponse.SUCCESS(pagedSubjectOrders);
    }

    private PagedList<SubjectOrder> buildPagedSubjectOrders(List<Order> orders, long totalCount, int start, int count) {
        Set<Long> orderIds = new HashSet<Long>();
        Set<Long> subjectIds = new HashSet<Long>();
        Map<Long, Long> orderCourse = new HashMap<Long, Long>();
        Set<Long> courseIds = new HashSet<Long>();
        for (Order order : orders) {
            orderIds.add(order.getId());
            subjectIds.add(order.getSubjectId());
            List<Long> orderCourseIds = order.getCourseIds();
            if (orderCourseIds.size() >= 1) {
                long courseId = orderCourseIds.get(0);
                orderCourse.put(order.getId(), courseId);
                courseIds.add(courseId);
            }
        }

        Map<Long, Integer> finishedCourceCounts = courseService.queryFinishedCourseCounts(orderIds);

        List<Subject> subjects = subjectService.list(subjectIds);
        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        List<Course> courses = courseService.list(courseIds);
        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<SubjectOrder> subjectOrders = new ArrayList<SubjectOrder>();
        for (Order order : orders) {
            String title;
            String cover;
            Long courseId = orderCourse.get(order.getId());
            if (courseId == null) {
                Subject subject = subjectsMap.get(order.getSubjectId());
                if (subject == null) continue;

                List<OrderPackage> orderPackages = order.getPackages();
                if (orderPackages.isEmpty()) continue;

                if (orderPackages.size() == 1) {
                    OrderPackage orderPackage = orderPackages.get(0);
                    title = orderPackage.getTitle();
                    cover = orderPackage.getCover();
                } else {
                    title = Configuration.getString("Package.MultiTitle");
                    cover = Configuration.getString("Package.MultiCover");
                }
            } else {
                Course course = coursesMap.get(courseId);
                if (course == null) continue;

                title = course.getTitle();
                cover = course.getCover();
            }

            SubjectOrder subjectOrder = buildBaseSubjectOrder(order, title, cover, finishedCourceCounts.get(order.getId()));
            if (courseId != null) subjectOrder.setCourseId(courseId);
            subjectOrders.add(subjectOrder);
        }

        PagedList<SubjectOrder> pagedSubjectOrders = new PagedList<SubjectOrder>(totalCount, start, count);
        pagedSubjectOrders.setList(subjectOrders);

        return pagedSubjectOrders;
    }

    @RequestMapping(value = "/bookable/package", method = RequestMethod.GET)
    public MomiaHttpResponse bookable(@RequestParam String utoken, @RequestParam(value = "coid") long courseId) {
//        try {
//            User user = userServiceApi.get(utoken);
//
//            if (courseService.hasNoAvaliableSkus(courseId)) return MomiaHttpResponse.SUCCESS(0);
//
//            List<Long> trialCourseIds = courseService.queryTrialCourseIds(courseId);
//            List<Long> trialSubjectIds = courseService.queryTrialSubjectIds(courseId);
//            Map<Long, List<SubjectSku>> trialSubjectSkusMap = subjectService.querySkus(trialSubjectIds);
//            Set<Long> trialSubjectSkuIds = new HashSet<Long>();
//            for (List<SubjectSku> trialSubjectSkus : trialSubjectSkusMap.values()) {
//                for (SubjectSku subjectSku : trialSubjectSkus) {
//                    if (subjectSku.getCourseId() == courseId || ) trialSubjectSkuIds.add(subjectSku.getId());
//                }
//            }
//            long subjectId = courseService.querySubjectId(courseId);
//            List<SubjectSku> normalSubjectSkus = subjectService.querySkus(subjectId);
//            Set<Long> normalSubjectSkuIds = new HashSet<Long>();
//            for (SubjectSku subjectSku : normalSubjectSkus) {
//                normalSubjectSkuIds.add(subjectSku.getId());
//            }
//
//            List<Long> excludedPackageIds = courseService.queryBookedPackageIds(user.getId(), courseId);
//            List<OrderPackage> packages = orderService.queryAllBookableByUser(user.getId());
//            Set<Long> packageIds = new HashSet<Long>();
//            for (OrderPackage orderPackage : packages) {
//                if (excludedPackageIds.contains(orderPackage.getId()) ||
//                        (!trialSubjectSkuIds.contains(orderPackage.getSkuId()) && !normalSubjectSkuIds.contains(orderPackage.getSkuId()))) continue;
//                packageIds.add(orderPackage.getId());
//            }
//
//            Map<Long, Date> startTimes = orderService.queryStartTimesOfPackages(packageIds);
//
//            List<PackageTime> trialPackageTimes = new ArrayList<PackageTime>();
//            List<PackageTime> normalPackageTimes = new ArrayList<PackageTime>();
//            for (OrderPackage orderPackage : packages) {
//                if (excludedPackageIds.contains(orderPackage.getId())) continue;
//
//                PackageTime packageTime = new PackageTime();
//                packageTime.setId(orderPackage.getId());
//                Date startTime = startTimes.get(orderPackage.getId());
//                if (startTime == null) packageTime.setExpireTime(null);
//                else packageTime.setExpireTime(TimeUtil.add(startTime, orderPackage.getTime(), orderPackage.getTimeUnit()));
//
//                if (trialSubjectSkuIds.contains(orderPackage.getSkuId())) trialPackageTimes.add(packageTime);
//                else normalPackageTimes.add(packageTime);
//            }
//
//            if (!trialPackageTimes.isEmpty()) {
//                Collections.sort(trialPackageTimes);
//                return MomiaHttpResponse.SUCCESS(trialPackageTimes.get(0).getId());
//            } else {
//                Collections.sort(normalPackageTimes);
//                return MomiaHttpResponse.SUCCESS(normalPackageTimes.get(0).getId());
//            }
//        } catch (Exception e) {
//            return MomiaHttpResponse.SUCCESS(0L);
//        }
        return MomiaHttpResponse.SUCCESS(0L);
    }

//    private static class PackageTime implements Comparable {
//        private long id;
//        private Date expireTime;
//
//        public long getId() {
//            return id;
//        }
//
//        public void setId(long id) {
//            this.id = id;
//        }
//
//        public Date getExpireTime() {
//            return expireTime;
//        }
//
//        public void setExpireTime(Date expireTime) {
//            this.expireTime = expireTime;
//        }
//
//        @Override
//        public int compareTo(Object o) {
//            if (this == o) return 0;
//            if (!(o instanceof PackageTime)) return -1;
//
//            PackageTime that = (PackageTime) o;
//            Date thisTime = expireTime;
//            Date thatTime = that.getExpireTime();
//
//            if (thatTime == null) return -1;
//            if (thisTime == null) return 1;
//
//            return thisTime.after(thatTime) ? 1 : -1;
//        }
//    }

    @RequestMapping(value = "/package/time/extend", method = RequestMethod.POST)
    public MomiaHttpResponse extendPackageTime(@RequestParam(value = "pid") long packageId, @RequestParam int time) {
        OrderPackage orderPackage = orderService.getOrderPackage(packageId);
        int originTime = orderPackage.getTime();
        int originTimeUnit = orderPackage.getTimeUnit();

        int newTime;
        int newTimeUnit = TimeUtil.TimeUnit.MONTH;

        switch (originTimeUnit) {
            case TimeUtil.TimeUnit.MONTH:
                newTime = originTime + time;
                break;
            case TimeUtil.TimeUnit.QUARTER:
                newTime = originTime * 3 + time;
                break;
            case TimeUtil.TimeUnit.YEAR:
                newTime = originTime * 12 + time;
                break;
            case TimeUtil.TimeUnit.HALF_YEAR:
                newTime = originTime * 6 + time;
                break;
            default: throw new MomiaErrorException("无效的课程包");
        }

        return MomiaHttpResponse.SUCCESS(orderService.extendPackageTime(packageId, newTime, newTimeUnit));
    }

    @RequestMapping(value = "/bookable/user", method = RequestMethod.GET)
    public MomiaHttpResponse queryBookableUserIds() {
        return MomiaHttpResponse.SUCCESS(orderService.queryBookableUserIds());
    }

    @RequestMapping(value = "/package/expired/user", method = RequestMethod.GET)
    public MomiaHttpResponse queryUserIdsOfPackagesToExpired(@RequestParam int days) {
        return MomiaHttpResponse.SUCCESS(orderService.queryUserIdsOfPackagesToExpired(days));
    }
}
