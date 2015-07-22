package cn.momia.service.deal;

import cn.momia.service.deal.exception.OrderLimitException;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PrepayResult;
import cn.momia.service.product.Product;
import cn.momia.service.promo.coupon.Coupon;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface DealServiceFacade {
    long placeOrder(Order order);
    void checkLimit(long userId, long skuId, int count, int limit) throws OrderLimitException;

    Order getOrder(long orderId);
    boolean deleteOrder(long userId, long orderId);
    long queryOrderCountByUser(long userId, int status);
    List<Order> queryOrderByUser(long userId, int status, int start, int count);

    List<Order> queryDistinctCustomerOrderByProduct(long productId, int start, int count);
    List<Order> queryAllCustomerOrderByProduct(long productId);

    boolean prepayOrder(long orderId);
    boolean payOrder(long orderId);

    PrepayResult prepay(HttpServletRequest request, Order order, Product product, Coupon coupon, int payType);
    boolean callback(Map<String, String> httpParams, int payType);
    boolean check(long userId, long orderId, long productId, long skuId);

    boolean logPayment(Payment payment);
}
