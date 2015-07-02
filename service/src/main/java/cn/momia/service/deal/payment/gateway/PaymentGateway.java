package cn.momia.service.deal.payment.gateway;

import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentGateway {
    Map<String,String> extractPrepayParams(HttpServletRequest request, Order order, Product product);
    PrepayResult prepay(PrepayParam param);
    CallbackResult callback(CallbackParam param);
}