package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.subject.Subject;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/subject/order")
public class OrderController extends BaseController {
    @Autowired private SubjectService subjectService;
    @Autowired private OrderService orderService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse placeOrder(@RequestBody Order order) {
        SubjectSku sku = subjectService.getSku(order.getSkuId());
        if (!checkOrder(order, sku)) return MomiaHttpResponse.FAILED("无效的订单数据");

        long orderId = orderService.add(order);
        if (orderId < 0) return MomiaHttpResponse.FAILED("下单失败");

        subjectService.increaseJoined(order.getSubjectId(), order.getCount() * sku.getJoinCount());

        order.setId(orderId);
        return MomiaHttpResponse.SUCCESS(buildOrderDto(order));
    }

    private boolean checkOrder(Order order, SubjectSku sku) {
        if (order.isInvalid()) return false;
        if (!sku.exists() || sku.getPrice().compareTo(order.getPrice()) != 0) return false;
        return true;
    }

    private OrderDto buildOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setSubjectId(order.getSubjectId());
        orderDto.setSkuId(order.getSkuId());
        orderDto.setCount(order.getCount());
        orderDto.setTotalFee(order.getTotalFee());

        return orderDto;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public MomiaHttpResponse listOrders(@RequestParam String utoken,
                                        @RequestParam int status,
                                        @RequestParam int start,
                                        @RequestParam int count) {
        UserDto user = userServiceApi.get(utoken);

        long totalCount = orderService.queryCountByUser(user.getId(), status);
        List<Order> orders = orderService.queryByUser(user.getId(), status, start, count);

        Set<Long> subjectIds = new HashSet<Long>();
        Set<Long> skuIds = new HashSet<Long>();
        for (Order order : orders) {
            subjectIds.add(order.getSubjectId());
            skuIds.add(order.getSkuId());
        }

        List<Subject> subjects = subjectService.list(subjectIds);
        Map<Long, Subject> subjectsMap = new HashMap<Long, Subject>();
        for (Subject subject : subjects) subjectsMap.put(subject.getId(), subject);

        List<SubjectSku> skus = subjectService.listSkus(skuIds);
        Map<Long, SubjectSku> skusMap = new HashMap<Long, SubjectSku>();
        for (SubjectSku sku : skus) skusMap.put(sku.getId(), sku);

        List<OrderDto> orderDtos = new ArrayList<OrderDto>();
        for (Order order : orders) {
            Subject subject = subjectsMap.get(order.getSubjectId());
            SubjectSku sku = skusMap.get(order.getSkuId());
            if (subject == null || sku == null) continue;

            orderDtos.add(buildOrderDetailDto(order, subject, sku));
        }

        PagedList<OrderDto> pagedOrderDtos = new PagedList<OrderDto>(totalCount, start, count);
        pagedOrderDtos.setList(orderDtos);

        return MomiaHttpResponse.SUCCESS(pagedOrderDtos);
    }

    private OrderDto buildOrderDetailDto(Order order, Subject subject, SubjectSku sku) {
        OrderDto orderDto = buildOrderDto(order);
        orderDto.setTotalCourseCount(order.getCount() * sku.getCourseCount());
        orderDto.setBookedCourseCount(order.getBookCourseCount());

        orderDto.setTitle(subject.getTitle());
        orderDto.setCover(subject.getCover());

        return orderDto;
    }
}
