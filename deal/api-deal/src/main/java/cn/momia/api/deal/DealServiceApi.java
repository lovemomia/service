package cn.momia.api.deal;

import cn.momia.api.deal.dto.CouponDto;
import cn.momia.api.deal.dto.OrderDto;
import cn.momia.api.deal.dto.SkuPlaymatesDto;
import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.entity.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.http.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DealServiceApi extends AbstractServiceApi {
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
        public OrderDto add(JSONObject orderJson) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("order"), orderJson.toString());
            return JSON.toJavaObject((JSON) executeRequest(request), OrderDto.class);
        }

        public JSON checkDup(JSONObject orderJson) {
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("order/check/dup"), orderJson.toString());
            return (JSON) executeRequest(request);
        }

        public void delete(String utoken, long id) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("order", id), builder.build());
            executeRequest(request);
        }

        public PagedList<OrderDto> listOrders(String utoken, int status, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("status", status)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/list"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), OrderDto.class);
        }

        public OrderDto get(String utoken, long orderId, long productId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("pid", productId);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order", orderId), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), OrderDto.class);
        }

        public List<String> listCustomerAvatars(long productId, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("pid", productId)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/customer"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), List.class);
        }

        public List<SkuPlaymatesDto> listPlaymates(long productId, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("pid", productId)
                    .add("start", 0)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/playmate"), builder.build());

            return CastUtil.toList((JSONArray) executeRequest(request), SkuPlaymatesDto.class);
        }

        public boolean check(String utoken, long orderId, long productId, long skuId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("pid", productId)
                    .add("sid", skuId);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order", orderId, "check"), builder.build());

            return (Boolean) executeRequest(request);
        }

        public List<Long> queryUserIds(long productId, long skuId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("pid", productId)
                    .add("sid", skuId);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/user"), builder.build());

            return JSON.toJavaObject((JSON) executeRequest(request), List.class);
        }
    }

    public static class PaymentServiceApi extends DealServiceApi {
        public Object prepayAlipay(String utoken, long orderId, long productId, long skuId, String type, Long coupon) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("oid", orderId)
                    .add("pid", productId)
                    .add("sid", skuId)
                    .add("type", type);
            if (coupon != null && coupon > 0) builder.add("coupon", coupon);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/alipay"), builder.build());

            return executeRequest(request);
        }

        public Object prepayWechatpay(String utoken, long orderId, long productId, long skuId, String tradeType, Long coupon, String code) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("oid", orderId)
                    .add("pid", productId)
                    .add("sid", skuId)
                    .add("trade_type", tradeType);
            if (coupon != null && coupon > 0) builder.add("coupon", coupon);
            if (!StringUtils.isBlank(code)) builder.add("code", code);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/wechatpay"), builder.build());

            return executeRequest(request);
        }

        public void prepayFree(String utoken, long orderId, long productId, long skuId, Long coupon) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("oid", orderId)
                    .add("pid", productId)
                    .add("sid", skuId);
            if (coupon != null && coupon > 0) builder.add("coupon", coupon);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("payment/prepay/free"), builder.build());
            executeRequest(request);
        }

        public void check(String utoken, long orderId, long productId, long skuId) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
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
        public PagedList<CouponDto> listCoupons(String utoken, long orderId, int status, int start, int count) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("oid", orderId)
                    .add("status", status)
                    .add("start", start)
                    .add("count", count);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("coupon/list"), builder.build());

            return CastUtil.toPagedList((JSONObject) executeRequest(request), CouponDto.class);
        }

        public BigDecimal calcTotalFee(String utoken, long orderId, long coupon) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                    .add("utoken", utoken)
                    .add("oid", orderId)
                    .add("coupon", coupon);
            MomiaHttpRequest request = MomiaHttpRequest.GET(url("coupon"), builder.build());

            return new BigDecimal(String.valueOf(executeRequest(request)));
        }

        public void distributeRegisterCoupon(String utoken) {
            MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest request = MomiaHttpRequest.POST(url("coupon/register"), builder.build());
            executeRequest(request);
        }
    }
}
