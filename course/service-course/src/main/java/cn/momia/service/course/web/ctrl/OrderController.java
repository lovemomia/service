package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.course.dto.OrderPackageDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderService;
import cn.momia.service.course.subject.order.OrderPackage;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/subject/order")
public class OrderController extends BaseController {
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private OrderService orderService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse placeOrder(@RequestBody Order order) {
        List<SubjectSku> orderSkus = order.getSkus();
        Set<Long> skuIds = new HashSet<Long>();
        for (SubjectSku sku : orderSkus) {
            skuIds.add(sku.getId());
        }
        List<SubjectSku> skus = subjectService.listSkus(skuIds);

        if (!checkAndCompleteOrder(order, skus)) return MomiaHttpResponse.FAILED("无效的订单数据");

        long orderId = orderService.add(order);
        if (orderId < 0) return MomiaHttpResponse.FAILED("下单失败");

        order.setId(orderId);
        return MomiaHttpResponse.SUCCESS(buildOrderDto(order));
    }

    private boolean checkAndCompleteOrder(Order order, List<SubjectSku> skus) {
        if (order.isInvalid()) return false;

        List<SubjectSku> orderSkus = order.getSkus();
        Map<Long, SubjectSku> skusMap = new HashMap<Long, SubjectSku>();
        for (SubjectSku sku : skus) {
            skusMap.put(sku.getId(), sku);
        }

        for (SubjectSku orderSku : orderSkus) {
            SubjectSku sku = skusMap.get(orderSku.getId());
            if (sku == null) return  false;
            orderSku.setPrice(sku.getPrice());
            orderSku.setCourseCount(sku.getCourseCount());
        }

        return true;
    }

    private OrderDto buildOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setCount(order.getCount());
        orderDto.setTotalFee(order.getTotalFee());
        orderDto.setAddTime(order.getAddTime());

        return orderDto;
    }

    @RequestMapping(value = "/bookable", method = RequestMethod.GET)
    public MomiaHttpResponse listBookableOrders(@RequestParam String utoken,
                                                @RequestParam int start,
                                                @RequestParam int count) {
        UserDto user = userServiceApi.get(utoken);

        long totalCount = orderService.queryBookableCountByUser(user.getId());
        List<OrderPackage> orderPackages = orderService.queryBookableByUser(user.getId(), start, count);

        PagedList<OrderPackageDto> pagedOrderSkuDtos = buildPagedOrderSkuDtos(totalCount, start, count, orderPackages);

        return MomiaHttpResponse.SUCCESS(pagedOrderSkuDtos);
    }

    private PagedList<OrderPackageDto> buildPagedOrderSkuDtos(long totalCount, int start, int count, List<OrderPackage> orderPackages) {
        Set<Long> packageIds = new HashSet<Long>();
        Set<Long> orderIds = new HashSet<Long>();
        for (OrderPackage orderPackage : orderPackages) {
            packageIds.add(orderPackage.getId());
            orderIds.add(orderPackage.getOrderId());
        }

        Map<Long, Date> startTimes = orderService.queryStartTimesByPackages(packageIds);

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
                orderPackageDto.setExpireTime("购买日期: " + TimeUtil.DATE_FORMAT.format(order.getAddTime()));
            } else {
                Date endTime = TimeUtil.add(startTime, sku.getTime(), sku.getTimeUnit());
                orderPackageDto.setExpireTime("有效期至: " + TimeUtil.DATE_FORMAT.format(endTime));
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
        UserDto user = userServiceApi.get(utoken);

        long totalCount = orderService.queryCountByUser(user.getId(), status);
        List<Order> orders = orderService.queryByUser(user.getId(), status, start, count);

        PagedList<OrderDto> pagedOrderDtos = buildPagedOrderDtos(totalCount, start, count, orders);

        return MomiaHttpResponse.SUCCESS(pagedOrderDtos);
    }

    private PagedList<OrderDto> buildPagedOrderDtos(long totalCount, int start, int count, List<Order> orders) {
        Set<Long> orderIds = new HashSet<Long>();
        Set<Long> subjectIds = new HashSet<Long>();
        for (Order order : orders) {
            orderIds.add(order.getId());
            subjectIds.add(order.getSubjectId());
        }

        Map<Long, Integer> bookedCourceCounts = courseService.queryBookedCourseCounts(orderIds);
        Map<Long, Integer> finishedCourceCounts = courseService.queryFinishedCourseCounts(orderIds);

        List<Subject> subjects = subjectService.list(subjectIds);
        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) {
            subjectsMap.put(subject.getId(), subject);
        }

        List<OrderDto> orderDtos = new ArrayList<OrderDto>();
        for (Order order : orders) {
            Subject subject = subjectsMap.get(order.getSubjectId());
            if (subject == null) continue;

            orderDtos.add(buildOrderDetailDto(order, subject, bookedCourceCounts, finishedCourceCounts));
        }

        PagedList<OrderDto> pagedOrderDtos = new PagedList<OrderDto>(totalCount, start, count);
        pagedOrderDtos.setList(orderDtos);

        return pagedOrderDtos;
    }

    private OrderDto buildOrderDetailDto(Order order, Subject subject, Map<Long, Integer> bookedCourceCounts, Map<Long, Integer> finishedCourceCounts) {
        OrderDto orderDto = buildOrderDto(order);
        orderDto.setTotalCourseCount(order.getTotalCourseCount());
        orderDto.setBookedCourseCount(bookedCourceCounts.get(order.getId()));
        orderDto.setFinishedCourseCount(finishedCourceCounts.get(order.getId()));

        orderDto.setTitle(subject.getTitle());
        orderDto.setCover(subject.getCover());

        return orderDto;
    }
}
