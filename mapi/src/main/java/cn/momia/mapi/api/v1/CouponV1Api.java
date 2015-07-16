package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon")
public class CouponV1Api extends AbstractV1Api {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage coupon(@RequestParam String utoken, @RequestParam(value = "oid") long orderId, @RequestParam long coupon) {
        if (StringUtils.isBlank(utoken) || orderId <= 0 || coupon <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("coupon", coupon);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("coupon"), builder.build());

        return executeRequest(request);
    }
}
