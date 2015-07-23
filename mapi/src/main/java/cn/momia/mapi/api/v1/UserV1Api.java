package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.img.ImageFile;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserV1Api extends AbstractV1Api {
    protected Function<Object, Object> orderDetailFunc = new Function<Object, Object>() {
        @Override
        public Object apply(Object data) {
            JSONObject orderDetailJson = (JSONObject) data;
            orderDetailJson.put("cover", ImageFile.url(orderDetailJson.getString("cover")));

            return orderDetailJson;
        }
    };

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.POST)
    public ResponseMessage updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(nickName)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("nickname", nickName);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/nickname"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(avatar)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/avatar"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/name", method = RequestMethod.POST)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(name)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/name"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.POST)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(sex)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/sex"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.POST)
    public ResponseMessage updateBirthday(@RequestParam String utoken, @RequestParam String birthday) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(birthday)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", birthday);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/birthday"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/city", method = RequestMethod.POST)
    public ResponseMessage updateCity(@RequestParam String utoken, @RequestParam int city) {
        if(StringUtils.isBlank(utoken) || city <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", city);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/city"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(address)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/address"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/child", method = RequestMethod.POST)
    public ResponseMessage addChild(@RequestParam String utoken, @RequestParam String children) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(children)) return ResponseMessage.BAD_REQUEST;

        JSONArray childrenJson = JSONArray.parseArray(children);
        long userId = getUserId(utoken);
        for (int i = 0; i < childrenJson.size(); i++) childrenJson.getJSONObject(i).put("userId", userId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("user/child"), childrenJson.toString());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/child/name", method = RequestMethod.POST)
    public ResponseMessage updateChildByName(@RequestParam String utoken,
                                             @RequestParam(value = "cid") long childId,
                                             @RequestParam String name) {
        if (StringUtils.isBlank(utoken) || childId <= 0 || StringUtils.isBlank(name)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("cid", childId)
                .add("name", name);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child/name"), builder.build());

        ResponseMessage response = executeRequest(request);
        if (!response.successful()) return response;

        return executeRequest(MomiaHttpRequest.GET(url("user"), new MomiaHttpParamBuilder().add("utoken", utoken).build()), userFunc);
    }


    @RequestMapping(value = "/child/sex", method = RequestMethod.POST)
    public ResponseMessage updateChildBySex(@RequestParam String utoken,
                                            @RequestParam(value = "cid") long childId,
                                            @RequestParam String sex) {
        if (StringUtils.isBlank(utoken) || childId <= 0 || StringUtils.isBlank(sex)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("cid", childId)
                .add("sex", sex);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child/sex"), builder.build());

        ResponseMessage response = executeRequest(request);
        if (!response.successful()) return response;

        return executeRequest(MomiaHttpRequest.GET(url("user"), new MomiaHttpParamBuilder().add("utoken", utoken).build()), userFunc);
    }

    @RequestMapping(value = "/child/birthday", method = RequestMethod.POST)
    public ResponseMessage updateChildByBirthday(@RequestParam String utoken,
                                                 @RequestParam(value = "cid") long childId,
                                                 @RequestParam String birthday) {
        if (StringUtils.isBlank(utoken) || childId <= 0 || StringUtils.isBlank(birthday)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("cid", childId)
                .add("birthday", birthday);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("user/child/birthday"), builder.build());

        ResponseMessage response = executeRequest(request);
        if (!response.successful()) return response;

        return executeRequest(MomiaHttpRequest.GET(url("user"), new MomiaHttpParamBuilder().add("utoken", utoken).build()), userFunc);
    }

    @RequestMapping(value = "/child/delete", method = RequestMethod.POST)
    public ResponseMessage deleteChild(@RequestParam String utoken, @RequestParam(value = "cid") long childId) {
        if(StringUtils.isBlank(utoken) || childId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("user/child", childId), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/child", method = RequestMethod.GET)
    public ResponseMessage getChild(@RequestParam String utoken, @RequestParam(value = "cid") long childId) {
        if(StringUtils.isBlank(utoken) || childId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/child", childId), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/child/list", method = RequestMethod.GET)
    public ResponseMessage getChildren(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/child"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @RequestParam(defaultValue = "1") int status,
                                           @RequestParam final int start) {
        if (StringUtils.isBlank(utoken) || start < 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status < 0 ? 1 : status)
                .add("start", start)
                .add("count", conf.getInt("Order.PageSize"));
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("order/user"), builder.build());

        return executeRequest(request, orderDetailFunc);
    }

    @RequestMapping(value = "/order/detail", method = RequestMethod.GET)
    public ResponseMessage getOrderDetailOfUser(@RequestParam String utoken,
                                                @RequestParam(value = "oid") long orderId,
                                                @RequestParam(value = "pid") long productId) {
        if (StringUtils.isBlank(utoken) || orderId <= 0 || productId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("pid", productId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("order", orderId), builder.build());

        return executeRequest(request, orderDetailFunc);
    }

    @RequestMapping(value = "/coupon", method = RequestMethod.GET)
    public ResponseMessage getCouponsOfUser(@RequestParam String utoken,
                                            @RequestParam(value = "oid", defaultValue = "0") long orderId,
                                            @RequestParam(defaultValue = "0") int status,
                                            @RequestParam final int start) {
        if (StringUtils.isBlank(utoken) || orderId < 0 || status < 0 || start < 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("status", status)
                .add("start", start)
                .add("count", conf.getInt("Coupon.PageSize"));
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("coupon/user"), builder.build());

        return executeRequest(request);
    }
}
