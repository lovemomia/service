package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.CallbackResult;
import cn.momia.service.deal.payment.gateway.PaymentGateway;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.PrepayResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class AlipayGateway implements PaymentGateway {
    @Override
    public Map<String, String> extractPrepayParams(HttpServletRequest request, Order order, Product product) {
        // TODO
        return null;
    }

    @Override
    public PrepayResult prepay(PrepayParam param) {
        // TODO
        return null;
    }

    @Override
    public CallbackResult callback(CallbackParam param) {
        // TODO
        return null;
    }
}
