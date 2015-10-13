package cn.momia.service.course.web.ctrl;

import cn.momia.api.course.dto.OrderDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.course.subject.SubjectService;
import cn.momia.service.course.subject.SubjectSku;
import cn.momia.service.course.subject.order.Order;
import cn.momia.service.course.subject.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/subject/order")
public class OrderController extends BaseController {
    @Autowired private SubjectService subjectService;
    @Autowired private OrderService orderService;

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
}
