package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.OrderDto;
import cn.momia.mapi.api.v1.dto.composite.OrderListDto;
import cn.momia.mapi.api.v1.dto.base.UserDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/v1/user")
public class UserApi extends AbstractApi {
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ResponseMessage viewUser(@RequestParam long id) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user", id));

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new UserDto.Other((JSONObject) data);
            }
        });
    }

    @RequestMapping(value = "/view/order", method = RequestMethod.GET)
    public ResponseMessage viewOrders(@RequestParam long id) {
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user", id, "order"));

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new UserDto.Own((JSONObject) data);
            }
        });
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken, @RequestParam int status, @RequestParam int start, @RequestParam final int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user/order"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                OrderListDto orders = new OrderListDto();

                JSONArray jsonArray = (JSONArray) data;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject order = jsonObject.getJSONObject("order");
                    JSONObject product = jsonObject.getJSONObject("product");
                    JSONObject sku = jsonObject.getJSONObject("sku");

                    if (product == null || sku == null) continue;

                    OrderDto orderDto = new OrderDto(order);
                    orderDto.setTitle(product.getString("title"));
                    orderDto.setTime(ProductUtil.getSkuTime(sku.getJSONArray("properties")));

                    orders.add(orderDto);
                }

                return orders;
            }
        });
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/avatar"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/name", method = RequestMethod.POST)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/name"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.POST)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/sex"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.POST)
    public ResponseMessage updateBirthday(@RequestParam String utoken, @RequestParam Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", birthday);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/birthday"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/city", method = RequestMethod.POST)
    public ResponseMessage updateCity(@RequestParam String utoken, @RequestParam int city) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", city);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/city"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/address"), builder.build());

        return executeRequest(request);
    }
}
