package cn.momia.service.deal.payment.gateway;

import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;

import java.util.Map;

public interface PaymentGateway {
    Map<String,String> extractPrepayParams(Map<String, String[]> parameterMap, Order order, Product product);
    PrepayResult prepay(PrepayParam param);
    CallbackResult callback(CallbackParam param);
}