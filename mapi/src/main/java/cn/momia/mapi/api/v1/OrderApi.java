package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpDeleteRequest;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/order")
public class OrderApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage placeOrder(@RequestParam String order) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("order", order);
        MomiaHttpRequest request = new MomiaHttpPostRequest(dealServiceUrl("order"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseMessage deleteOrder(@PathVariable long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(dealServiceUrl("order", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken, @RequestParam String query) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("query", query);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user/order"), builder.build());

        return executeRequest(request);
    }
}
