package cn.momia.service.deal.api;

import cn.momia.api.base.ServiceApi;
import cn.momia.api.base.http.MomiaHttpParamBuilder;
import cn.momia.api.base.http.MomiaHttpRequest;
import cn.momia.service.deal.api.coupon.PagedCoupons;
import cn.momia.service.deal.api.order.Order;
import cn.momia.service.deal.api.order.PagedOrders;
import cn.momia.service.deal.api.order.SkuPlaymates;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DealServiceApi extends ServiceApi {
    public static OrderServiceApi ORDER = new OrderServiceApi();
    public static PaymentServiceApi PAYMENT = new PaymentServiceApi();
    public static CallbackServiceApi CALLBACK = new CallbackServiceApi();
    public static CouponServiceApi COUPON = new CouponServiceApi();

    public void init() {
        ORDER.setService(service);
        PAYMENT.setService(service);
        CALLBACK.setService(service);
        COUPON.setService(service);
    }

    public static class OrderServiceApi extends DealServiceApi {
        public void add(JSONObject orderJson) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("order"), orderJson.toString());
            executeRequest(request);
        }

        public PagedOrders listOrders(long userId, int status, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("status", status)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/list"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), PagedOrders.class);
        }

        public Order get(long userId, long orderId, long productId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("pid", productId);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order", orderId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), Order.class);
        }

        public List<String> listCustomerAvatars(long productId, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("pid", productId)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/customer"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), List.class);
        }

        public List<SkuPlaymates> listPlaymates(long productId, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("pid", productId)
                    .add("start", 0)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/playmate"), builder.build());
            JSONArray playmatesJson = (JSONArray) executeRequest(request);

            List<SkuPlaymates> playmates = new ArrayList<SkuPlaymates>();
            for (int i = 0; i < playmatesJson.size(); i++) {
                JSONObject skuPlaymatesJson = playmatesJson.getJSONObject(i);
                playmates.add(JSON.toJavaObject(skuPlaymatesJson, SkuPlaymates.class));
            }

            return playmates;
        }

        public void delete(long userId, long id) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uid", userId);
            MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("order", id), builder.build());
            executeRequest(request);
        }
    }

    public static class PaymentServiceApi extends DealServiceApi {
        public Object prepayAlipay(long userId, long orderId, long productId, long skuId, String type, Long coupon) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("oid", orderId)
                    .add("pid", productId)
                    .add("sid", skuId)
                    .add("type", type);
            if (coupon != null && coupon > 0) builder.add("coupon", coupon);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/alipay"), builder.build());

            return executeRequest(request);
        }

        public Object prepayWechatpay(long userId, long orderId, long productId, long skuId, String tradeType, Long coupon, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("oid", orderId)
                    .add("pid", productId)
                    .add("sid", skuId)
                    .add("trade_type", tradeType);
            if (coupon != null && coupon > 0) builder.add("coupon", coupon);
            if (!StringUtils.isBlank(code)) builder.add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/wechatpay"), builder.build());

            return executeRequest(request);
        }

        public void prepayFree(long userId, long orderId, long productId, long skuId, Long coupon) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("oid", orderId)
                    .add("pid", productId)
                    .add("sid", skuId);
            if (coupon != null && coupon > 0) builder.add("coupon", coupon);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/free"), builder.build());
            executeRequest(request);
        }

        public void check(long userId, long orderId, long productId, long skuId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("oid", orderId)
                    .add("pid", productId)
                    .add("sid", skuId);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("payment/check"), builder.build());
            executeRequest(request);
        }
    }

    public static class CallbackServiceApi extends DealServiceApi {
        public boolean callbackAlipay(Map<String, String> params) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("callback/alipay"), params);
            return "OK".equalsIgnoreCase((String) executeRequest(request));
        }

        public boolean callbackWechatpay(Map<String, String> params) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("callback/wechatpay"), params);
            return "OK".equalsIgnoreCase((String) executeRequest(request));
        }
    }

    public static class CouponServiceApi extends DealServiceApi {
        public PagedCoupons listCoupons(long userId, long orderId, int status, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("oid", orderId)
                    .add("status", status)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("coupon/list"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), PagedCoupons.class);
        }

        public BigDecimal calcTotalFee(long userId, long orderId, long coupon) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("uid", userId)
                    .add("oid", orderId)
                    .add("coupon", coupon);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("coupon"), builder.build());

            return new BigDecimal((String) executeRequest(request));
        }
    }
}
