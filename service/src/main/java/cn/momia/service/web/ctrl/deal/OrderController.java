package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.sku.SkuPrice;
import cn.momia.service.user.base.User;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderPrice;
import cn.momia.service.web.ctrl.AbstractController;
import cn.momia.service.web.ctrl.deal.dto.OrderDetailDto;
import cn.momia.service.web.ctrl.deal.dto.OrderDto;
import cn.momia.service.web.ctrl.dto.PagedListDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage placeOrder(@RequestBody Order order) {
        Sku sku = productServiceFacade.getSku(order.getSkuId());
        if (!checkOrder(order, sku)) return ResponseMessage.FAILED("无效的订单");

        if (!lockSku(order)) return ResponseMessage.FAILED("库存不足");

        long orderId = 0;
        try {
            userServiceFacade.processContacts(order.getCustomerId(), order.getMobile(), order.getContacts());
            dealServiceFacade.checkLimit(order.getCustomerId(), sku.getId(), order.getCount(), sku.getLimit());

            orderId = dealServiceFacade.placeOrder(order);
            if (orderId > 0) {
                order.setId(orderId);
                return new ResponseMessage(new OrderDto(order));
            }
        } catch (OrderLimitException e) {
            return ResponseMessage.FAILED("本单有限购，您已超出购买限额");
        } catch (Exception e) {
            LOGGER.error("fail to place order, customerId: {}, productId: {}, skuId: {}", new Object[] { order.getCustomerId(), order.getProductId(), order.getSkuId(), e });
        } finally {
            // TODO 需要告警
            if (orderId <= 0 && !unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });
        }

        return ResponseMessage.FAILED("下单失败");
    }

    private boolean checkOrder(Order order, Sku sku) {
        if (order.getCustomerId() <= 0 ||
                order.getProductId() <= 0 ||
                order.getSkuId() <= 0 ||
                order.getPrices().isEmpty() ||
                StringUtils.isBlank(order.getMobile()) ||
                order.getProductId() != sku.getProductId() ||
                sku.isClosed(new Date())) return false;

        for (OrderPrice price : order.getPrices()) {
            boolean found = false;
            List<SkuPrice> skuPrices = sku.getPrice(price.getAdult(), price.getChild());
            for (SkuPrice skuPrice : skuPrices) {
                if (price.getPrice().compareTo(skuPrice.getPrice()) == 0) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }

    private boolean lockSku(Order order) {
        return productServiceFacade.lockStock(order.getProductId(), order.getSkuId(), order.getCount());
    }

    private boolean unlockSku(Order order) {
        return productServiceFacade.unlockStock(order.getProductId(), order.getSkuId(), order.getCount());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteOrder(@RequestParam String utoken, @PathVariable long id) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = dealServiceFacade.getOrder(id);
        if (!order.exists() || order.getCustomerId() != user.getId()) return ResponseMessage.FAILED("无效的订单");

        if (!dealServiceFacade.deleteOrder(user.getId(), id)) return ResponseMessage.FAILED("删除订单失败");

        // TODO 需要告警
        if (!unlockSku(order)) LOGGER.error("fail to unlock sku, skuId: {}, count: {}", new Object[] { order.getSkuId(), order.getCount() });

        int status = order.getStatus();
        if (status == Order.Status.PRE_PAYED) promoServiceFacade.releaseUserCoupon(order.getCustomerId(), order.getId());

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @RequestParam int status,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        if (isInvalidLimit(start, count)) return new ResponseMessage(PagedListDto.EMPTY);

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        long totalCount = dealServiceFacade.queryOrderCountByUser(user.getId(), status);
        List<Order> orders = dealServiceFacade.queryOrderByUser(user.getId(), status, start, count);

        List<Long> productIds = new ArrayList<Long>();
        for (Order order : orders) productIds.add(order.getProductId());
        List<Product> products = productServiceFacade.get(productIds);

        return new ResponseMessage(buildUserOrders(totalCount, orders, products, start, count));
    }

    private PagedListDto buildUserOrders(long totalCount, List<Order> orders, List<Product> products, int start, int count) {
        Map<Long, Product> productMap = new HashMap<Long, Product>();
        for (Product product : products) productMap.put(product.getId(), product);

        PagedListDto userOrdersDto = new PagedListDto(totalCount, start, count);
        for (Order order : orders) {
            try {
                Product product = productMap.get(order.getProductId());
                if (product == null) continue;

                userOrdersDto.add(new OrderDetailDto(order, product));
            } catch (Exception e) {
                LOGGER.error("fail to build order dto for order: {}", order.getId(), e);
            }
        }

        return userOrdersDto;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getOrderOfUser(@RequestParam String utoken,
                                           @PathVariable(value = "id") long id,
                                           @RequestParam(value = "pid") long productId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = dealServiceFacade.getOrder(id);
        Product product = productServiceFacade.get(productId);
        if (!order.exists() || !product.exists() ||
                order.getCustomerId() != user.getId() ||
                order.getProductId() != product.getId()) return ResponseMessage.FAILED("无效的订单");

        return new ResponseMessage(new OrderDetailDto(order, product));
    }
}
