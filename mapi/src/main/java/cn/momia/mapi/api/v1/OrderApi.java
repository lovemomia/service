package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/order")
public class OrderApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage placeOrder(@RequestParam(value = "order") String orderJson) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("order", orderJson);
        MomiaHttpRequest request = new MomiaHttpPostRequest(dealServiceUrl("order"), builder.build());

        return executeRequest(request);
    }
}
