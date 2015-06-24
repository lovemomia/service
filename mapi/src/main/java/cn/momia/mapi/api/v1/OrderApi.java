package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.Dto;
import cn.momia.mapi.api.v1.dto.OrderDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/order")
public class OrderApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage placeOrder(@RequestParam String utoken, @RequestParam final String order) {
        ResponseMessage userResponse = getUser(utoken);
        if (userResponse.getErrno() != ErrorCode.SUCCESS) return new ResponseMessage(ErrorCode.FORBIDDEN, userResponse.getErrmsg());

        long userId = ((JSONObject) userResponse.getData()).getLong("id");
        JSONObject orderJson = JSON.parseObject(order);
        orderJson.put("customerId", userId);

        MomiaHttpRequest request = MomiaHttpRequest.POST(dealServiceUrl("order"), orderJson.toString());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                JSONObject orderJson = (JSONObject) data;

                return new OrderDto(orderJson.getInteger("count"), orderJson.getFloat("totalFee"));
            }
        });
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteOrder(@RequestParam long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(dealServiceUrl("order", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken, @RequestParam String query) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("query", query);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("order"), builder.build());

        return executeRequest(request);
    }
}
