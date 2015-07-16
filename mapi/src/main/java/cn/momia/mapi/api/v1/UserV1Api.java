package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.OrderDto;
import cn.momia.mapi.api.v1.dto.base.ParticipantDto;
import cn.momia.mapi.api.v1.dto.base.UserCouponDto;
import cn.momia.mapi.api.v1.dto.composite.ListDto;
import cn.momia.mapi.api.v1.dto.composite.PagedListDto;
import cn.momia.mapi.api.v1.dto.base.UserDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserV1Api extends AbstractV1Api {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserV1Api.class);

    private static final Function<Object, Dto> userFunc = new Function<Object, Dto>() {
        @Override
        public Dto apply(Object data) {
            return new UserDto((JSONObject) data);
        }
    };

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @RequestParam int status,
                                           @RequestParam final int start) {
        final int pageSize = conf.getInt("Order.PageSize");
        final int maxPageCount = conf.getInt("Order.MaxPageCount");
        if (StringUtils.isBlank(utoken) || start < 0 || start > pageSize * maxPageCount) return new ResponseMessage(PagedListDto.EMPTY);

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("start", start)
                .add("count", pageSize);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/order"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return buildOrdersDto((JSONObject) data, start, pageSize);
            }
        });
    }

    private Dto buildOrdersDto(JSONObject ordersPackJson, int start, int count) {
        PagedListDto orders = new PagedListDto();

        long totalCount = ordersPackJson.getLong("totalCount");
        orders.setTotalCount(totalCount);

        JSONArray ordersJson = ordersPackJson.getJSONArray("orders");
        for (int i = 0; i < ordersJson.size(); i++) {
            try {
                orders.add(new OrderDto(ordersJson.getJSONObject(i), true));
            } catch (Exception e) {
                LOGGER.error("fail to parse order: {}", ordersJson.getJSONObject(i), e);
            }
        }
        if (start + count < totalCount) orders.setNextIndex(start + count);

        return orders;
    }

    @RequestMapping(value = "/order/detail", method = RequestMethod.GET)
    public ResponseMessage getOrderDetailOfUser(@RequestParam String utoken,
                                                @RequestParam(value = "oid") long orderId,
                                                @RequestParam(value = "pid") long productId) {
        if (StringUtils.isBlank(utoken) || orderId <= 0 || productId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("pid", productId);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/order", orderId), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new OrderDto((JSONObject) data, true);
            }
        });
    }

    @RequestMapping(value = "/coupon", method = RequestMethod.GET)
    public ResponseMessage getCouponsOfUser(@RequestParam String utoken,
                                            @RequestParam(value = "oid", defaultValue = "0") long orderId,
                                            @RequestParam(defaultValue = "0") int status,
                                            @RequestParam final int start) {
        final int pageSize = conf.getInt("Coupon.PageSize");
        final int maxPageCount = conf.getInt("Coupon.MaxPageCount");
        if (StringUtils.isBlank(utoken) || start < 0 || start > pageSize * maxPageCount) return new ResponseMessage(PagedListDto.EMPTY);

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("oid", orderId)
                .add("status", status)
                .add("start", start)
                .add("count", pageSize);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/coupon"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return buildUserCouponsDto((JSONObject) data, start, pageSize);
            }
        });
    }

    private Dto buildUserCouponsDto(JSONObject userCouponsPackJson, int start, int count) {
        PagedListDto userCoupons = new PagedListDto();

        int totalCount = userCouponsPackJson.getInteger("totalCount");
        userCoupons.setTotalCount(totalCount);

        JSONArray userCouponsJson = userCouponsPackJson.getJSONArray("userCoupons");
        JSONObject couponsJson = userCouponsPackJson.getJSONObject("coupons");
        for (int i = 0; i < userCouponsJson.size(); i++) {
            try {
                JSONObject userCouponJson = userCouponsJson.getJSONObject(i);
                JSONObject couponJson = couponsJson.getJSONObject(userCouponJson.getString("couponId"));
                if (couponJson == null) continue;

                userCoupons.add(new UserCouponDto(userCouponJson, couponJson));
            } catch (Exception e) {
                LOGGER.error("fail to parse user coupon: {}", userCouponsJson.getJSONObject(i), e);
            }
        }
        if (start + count < totalCount) userCoupons.setNextIndex(start + count);

        return userCoupons;
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

        long userId = getUserId(utoken);
        if (userId <= 0) return ResponseMessage.TOKEN_EXPIRED;

        JSONArray childrenJson = JSONArray.parseArray(children);
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
                .add("id", childId)
                .add("name", name);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("participant/name"), builder.build());

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
                .add("id", childId)
                .add("sex", sex);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("participant/sex"), builder.build());

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
                .add("id", childId)
                .add("birthday", birthday);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("participant/birthday"), builder.build());

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

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new ParticipantDto((JSONObject) data);
            }
        });
    }

    @RequestMapping(value = "/child/list", method = RequestMethod.GET)
    public ResponseMessage getChildren(@RequestParam String utoken) {
        if(StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("user/child"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return buildChildrenDto((JSONArray) data);
            }
        });
    }

    private Dto buildChildrenDto(JSONArray childrenJson) {
        ListDto children = new ListDto();
        for (int i = 0; i < childrenJson.size(); i++) {
            try {
                JSONObject childJson = childrenJson.getJSONObject(i);
                children.add(new ParticipantDto(childJson));
            } catch (Exception e) {
                LOGGER.error("invalid child: {}", childrenJson);
            }
        }

        return children;
    }
}
