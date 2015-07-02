package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.AbstractApi;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.WechatpayPrepayDto;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/payment")
public class PaymentApi extends AbstractApi {
    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public ResponseMessage prepayWechatpay(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            params.put(entry.getKey(), entry.getValue()[0]);
        }

        return executeRequest(MomiaHttpRequest.POST(dealServiceUrl("payment/prepay/wechatpay"), params), new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                WechatpayPrepayDto wechatpayPrepayDto = new WechatpayPrepayDto();
                JSONObject prepayJson = (JSONObject) data;
                wechatpayPrepayDto.setSuccessful(prepayJson.getBoolean("successful"));
                if (wechatpayPrepayDto.isSuccessful()) {
                    JSONObject paramJson = prepayJson.getJSONObject("all");
                    wechatpayPrepayDto.setAppId(paramJson.getString("appid"));
                    wechatpayPrepayDto.setMchId(paramJson.getString("mch_id"));
                    wechatpayPrepayDto.setPrepayId(paramJson.getString("prepay_id"));
                    wechatpayPrepayDto.setNonceStr(paramJson.getString("nonce_str"));
                    wechatpayPrepayDto.setSignType("MD5");
                    wechatpayPrepayDto.setSign(paramJson.getString("sign"));
                }

                return wechatpayPrepayDto;
            }
        });
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseMessage checkPayment(@RequestParam String utoken, @RequestParam(value = "oid") long orderId, @RequestParam(value = "pid") long productId, @RequestParam(value = "sid") long skuId) {
        if (StringUtils.isBlank(utoken) || productId <= 0 || skuId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("pid", productId)
                .add("sid", skuId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("payment/check"), builder.build());

        return executeRequest(request);
    }
}
