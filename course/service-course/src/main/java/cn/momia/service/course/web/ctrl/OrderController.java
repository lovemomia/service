package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.OrderPackageDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.exception.MomiaErrorException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseService;
import cn.momia.api.course.dto.UserCoupon;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.api.course.dto.SubjectSku;
import cn.momia.service.course.coupon.CouponService;
import cn.momia.service.course.order.Order;
import cn.momia.service.course.order.OrderService;
import cn.momia.service.course.order.OrderPackage;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            return MomiaHttpResponse.SUCCESS(buildOrderDto(order));
        } catch (Exception e) {
            return MomiaHttpResponse.FAILED("下单失败");
        } finally {
            if (orderId <= 0 && isTrial) subjectService.increaseStock(order.getSubjectId(), order.getCount());
        }
    }

    private boolean checkAndCompleteOrder(Order order, List<SubjectSku> skus) {
        if (order.isInvalid()) return false;

        User user = userServiceApi.get(order.getUserId());
        if (subjectService.isTrial(order.getSubjectId()) && (user.isPayed() || orderService.hasTrialOrder(user.getId()))) throw new MomiaErrorException("本课程包只供新用户专享");

        Map<Long, SubjectSku> skusMap = new HashMap<Long, SubjectSku>();
        for (SubjectSku sku : skus) {
            if (sku.getSubjectId() != order.getSubjectId()) return false;
            skusMap.put(sku.getId(), sku);
        }

        List<OrderPackage> orderPackages = order.getPackages();
        for (OrderPackage orderPackage : orderPackages) {
            SubjectSku sku = skusMap.get(orderPackage.getSkuId());
            if (sku == null) return  false;

            if (sku.getLimit() > 0) checkLimit(order.getUserId(), sku.getId(), sku.getLimit());

            orderPackage.setPrice(sku.getPrice());
            orderPackage.setBookableCount(sku.getCourseCount());
        }

        return true;
    }

    private void checkLimit(long userId, long skuId, int limit) {
        int boughtCount = orderService.getBoughtCount(userId, skuId);
        if (boughtCount > limit) throw new MomiaErrorException("超出购买限额");
    }

    private OrderDto buildOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setSubjectId(order.getSubjectId());
        orderDto.setCount(order.getCount());
        orderDto.setTotalFee(order.getTotalFee());
        orderDto.setStatus(order.getStatus() <= Order.Status.PRE_PAYED ? Order.Status.PRE_PAYED : order.getStatus());
        orderDto.setAddTime(order.getAddTime());

        return orderDto;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam String utoken, @RequestParam(value = "oid") long orderId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(orderService.delete(user.getId(), orderId));
    }

    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public MomiaHttpResponse refund(@RequestParam String utoken, @RequestParam(value = "oid") long orderId) {
        if (courseService.queryBookedCourseCounts(Sets.newHashSet(orderId)).get(orderId) > 0) return MomiaHttpResponse.FAILED("已经选过课的订单不能申请退款");

        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(orderService.refund(user.getId(), orderId));
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
            Subject subject = subjectService.get(order.getSubjectId());
            title = subject.getTitle();
            cover = subject.getCover();
        }
        Map<Long, Integer> finishedCourceCounts = courseService.queryFinishedCourseCounts(Sets.newHashSet(orderId));
        OrderDto orderDetailDto = buildOrderDetailDto(order, title, cover, finishedCourceCounts.get(orderId));
        if (courseIds.size() >= 1) orderDetailDto.setCourseId(courseIds.get(0));

        if (order.isPayed()) {
            UserCoupon userCoupon = couponService.queryUsedByOrder(orderId);
            if (userCoupon.exists()) {
                orderDetailDto.setUserCouponId(userCoupon.getId());
                orderDetailDto.setCouponId(userCoupon.getCouponId());
                orderDetailDto.setCouponType(userCoupon.getType());
                orderDetailDto.setDiscount(userCoupon.getDiscount());
                orderDetailDto.setCouponDesc(userCoupon.getDiscount() + "元红包"); // TODO 更多类型
            }
        }

        return MomiaHttpResponse.SUCCESS(orderDetailDto);
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

        PagedList<OrderPackageDto> pagedOrderPackageDtos = buildPagedOrderPackageDtos(orderPackages, totalCount, start, count);

        return MomiaHttpResponse.SUCCESS(pagedOrderPackageDtos);
    }

    private PagedList<OrderPackageDto> buildPagedOrderPackageDtos(List<OrderPackage> orderPackages, long totalCount, int start, int count) {
        Set<Long> packageIds = new HashSet<Long>();
        Set<Long> orderIds = new HashSet<Long>();
        Set<Long> courseIds = new HashSet<Long>();
        for (OrderPackage orderPackage : orderPackages) {
            packageIds.add(orderPackage.getId());
            orderIds.add(orderPackage.getOrderId());
            if (orderPackage.getCourseId() > 0) courseIds.add(orderPackage.getCourseId());
        }

        Map<Long, Date> startTimes = courseService.queryStartTimesByPackages(packageIds);
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

        List<OrderPackageDto> orderPackageDtos = new ArrayList<OrderPackageDto>();
        for (OrderPackage orderPackage : orderPackages) {
            Order order = ordersMap.get(orderPackage.getOrderId());
            if (order == null) continue;
            Subject subject = subjectsMap.get(order.getSubjectId());
            if (subject == null) continue;
            SubjectSku sku = subject.getSku(orderPackage.getSkuId());
            if (!sku.exists()) continue;

            OrderPackageDto orderPackageDto = new OrderPackageDto();
            orderPackageDto.setPackageId(orderPackage.getId());
            orderPackageDto.setSubjectId(order.getSubjectId());
            orderPackageDto.setTitle(subject.getTitle());
            orderPackageDto.setCover(subject.getCover());
            orderPackageDto.setBookableCourseCount(orderPackage.getBookableCount());

            Date startTime = startTimes.get(orderPackage.getId());
            if (startTime == null) {
                orderPackageDto.setExpireTime("购买日期: " + TimeUtil.SHORT_DATE_FORMAT.format(order.getAddTime()));
            } else {
                Date endTime = TimeUtil.add(startTime, sku.getTime(), sku.getTimeUnit());
                orderPackageDto.setExpireTime("有效期至: " + TimeUtil.SHORT_DATE_FORMAT.format(endTime));
            }

            orderPackageDto.setCourseId(orderPackage.getCourseId());
            Course course = coursesMap.get(orderPackage.getCourseId());
            if (course != null) {
                orderPackageDto.setCover(course.getCover());
                orderPackageDto.setTitle(course.getTitle());
            }

            orderPackageDtos.add(orderPackageDto);
        }

        PagedList<OrderPackageDto> pagedOrderSkuDtos = new PagedList<OrderPackageDto>(totalCount, start, count);
        pagedOrderSkuDtos.setList(orderPackageDtos);

        return pagedOrderSkuDtos;
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

        PagedList<OrderDto> pagedOrderDtos = buildPagedOrderDtos(orders, totalCount, start, count);

        return MomiaHttpResponse.SUCCESS(pagedOrderDtos);
    }

    private PagedList<OrderDto> buildPagedOrderDtos(List<Order> orders, long totalCount, int start, int count) {
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

        List<OrderDto> orderDtos = new ArrayList<OrderDto>();
        for (Order order : orders) {
            String title;
            String cover;
            Long courseId = orderCourse.get(order.getId());
            if (courseId == null) {
                Subject subject = subjectsMap.get(order.getSubjectId());
                if (subject == null) continue;

                title = subject.getTitle();
                cover = subject.getCover();
            } else {
                Course course = coursesMap.get(courseId);
                if (course == null) continue;

                title = course.getTitle();
                cover = course.getCover();
            }

            OrderDto orderDto = buildOrderDetailDto(order, title, cover, finishedCourceCounts.get(order.getId()));
            if (courseId != null) orderDto.setCourseId(courseId);
            orderDtos.add(orderDto);
        }

        PagedList<OrderDto> pagedOrderDtos = new PagedList<OrderDto>(totalCount, start, count);
        pagedOrderDtos.setList(orderDtos);

        return pagedOrderDtos;
    }

    private OrderDto buildOrderDetailDto(Order order, String title, String cover, int finishedCourseCount) {
        OrderDto orderDto = buildOrderDto(order);
        int bookableCourseCount = order.getBookableCourseCount();
        if (bookableCourseCount > 0) {
            orderDto.setBookingStatus(1);
        } else {
            int totalCourseCount = order.getTotalCourseCount();
            if (finishedCourseCount < totalCourseCount) orderDto.setBookingStatus(2);
            else orderDto.setBookingStatus(3);
        }
        orderDto.setTitle(title);
        orderDto.setCover(cover);

        return orderDto;
    }
}
