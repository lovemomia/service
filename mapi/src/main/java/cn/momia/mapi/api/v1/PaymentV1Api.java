package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.base.AlipayPrepayDto;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.WechatpayPrepayDto;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payment")
public class PaymentV1Api extends AbstractV1Api {
    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public ResponseMessage prepayAlipay(@RequestParam String utoken,
                                        @RequestParam(value = "oid") long orderId,
                                        @RequestParam(value = "pid") long productId,
                                        @RequestParam(value = "sid") long skuId,
                                        @RequestParam(required = false) Long coupon) {
        if (StringUtils.isBlank(utoken) ||
                orderId <= 0||
                productId <= 0 ||
                skuId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("pid", productId)
                .add("sid", skuId);
        if (coupon != null && coupon > 0) builder.add("coupon", coupon);
        MomiaHttpRequest request = MomiaHttpRequest.POST(dealServiceUrl("payment/prepay/alipay"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new AlipayPrepayDto((JSONObject) data);
            }
        });
    }

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(@RequestParam String utoken,
                                           @RequestParam(value = "oid") long orderId,
                                           @RequestParam(value = "pid") long productId,
                                           @RequestParam(value = "sid") long skuId,
                                           @RequestParam(value = "trade_type") final String tradeType,
                                           @RequestParam(required = false) Long coupon,
                                           @RequestParam(required = false) String code) {
        if (StringUtils.isBlank(utoken) ||
                orderId <= 0||
                productId <= 0 ||
                skuId <= 0 ||
                StringUtils.isBlank(tradeType)) return ResponseMessage.BAD_REQUEST;

        if (tradeType.equals("JSAPI") && StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("pid", productId)
                .add("sid", skuId)
                .add("trade_type", tradeType);
        if (coupon != null && coupon > 0) builder.add("coupon", coupon);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        MomiaHttpRequest request = MomiaHttpRequest.POST(dealServiceUrl("payment/prepay/wechatpay"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new WechatpayPrepayDto((JSONObject) data, tradeType);
            }
        });
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseMessage checkPayment(@RequestParam String utoken,
                                        @RequestParam(value = "oid") long orderId,
                                        @RequestParam(value = "pid") long productId,
                                        @RequestParam(value = "sid") long skuId) {
        if (StringUtils.isBlank(utoken) ||
                orderId <= 0 ||
                productId <= 0 ||
                skuId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("pid", productId)
                .add("sid", skuId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("payment/check"), builder.build());

        return executeRequest(request);
    }
}
