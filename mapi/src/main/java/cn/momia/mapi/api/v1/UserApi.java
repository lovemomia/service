package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.AbstractApi;
import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.OrderDto;
import cn.momia.mapi.api.v1.dto.base.ParticipantDto;
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

import java.util.Date;

@RestController
@RequestMapping("/v1/user")
public class UserApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserApi.class);
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
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken, @RequestParam int status, @RequestParam(defaultValue = "eq") String type, @RequestParam final int start, @RequestParam final int count) {
        final int maxPageCount = conf.getInt("Order.MaxPageCount");
        final int pageSize = conf.getInt("Order.PageSize");
        if (StringUtils.isBlank(utoken) || start < 0 || count <= 0 || start > maxPageCount * pageSize) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("status", status)
                .add("type", type)
                .add("start", start)
                .add("count", count);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user/order"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                PagedListDto<OrderDto> orders = new PagedListDto<OrderDto>();

                JSONObject ordersPackJson = (JSONObject) data;
                final long totalCount = ordersPackJson.getLong("totalCount");
                orders.setTotalCount(totalCount);
                JSONArray ordersJson = ordersPackJson.getJSONArray("orders");
                for (int i = 0; i < ordersJson.size(); i++) {
                    try {
                        JSONObject orderPackJson = ordersJson.getJSONObject(i);
                        JSONObject orderJson = orderPackJson.getJSONObject("order");

                        OrderDto orderDto = new OrderDto(orderJson);
                        orderDto.setTitle(orderPackJson.getString("product"));
                        orderDto.setTime(ProductUtil.getSkuScheduler(orderPackJson.getJSONArray("sku")));

                        orders.add(orderDto);
                    } catch (Exception e) {
                        LOGGER.error("fail to parse order: {}", ordersJson.getJSONObject(i), e);
                    }
                }
                if (start + count < totalCount) orders.setNextIndex(start + count);

                return orders;
            }
        });
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.POST)
    public ResponseMessage updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(nickName)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("nickname", nickName);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/nickname"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(avatar)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/avatar"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/name", method = RequestMethod.POST)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(name)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/name"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.POST)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(sex)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/sex"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.POST)
    public ResponseMessage updateBirthday(@RequestParam String utoken, @RequestParam String birthday) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(birthday)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", birthday);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/birthday"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/city", method = RequestMethod.POST)
    public ResponseMessage updateCity(@RequestParam String utoken, @RequestParam int city) {
        if(StringUtils.isBlank(utoken) || city <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", city);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/city"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(address)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("user/address"), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/child", method = RequestMethod.POST)
    public ResponseMessage addChild(@RequestParam String utoken, @RequestParam String child) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(child)) return ResponseMessage.BAD_REQUEST;

        JSONArray childJsonArray = JSONArray.parseArray(child);
        for(int i=0; i<childJsonArray.size(); i++)
            childJsonArray.getJSONObject(i).put("userId", getUserId(utoken));
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.POST(baseServiceUrl("user/child"), childJsonArray.toString());

        if (executeRequest(request).getErrno() != 0) return executeRequest(request);

        MomiaHttpRequest requestUser = MomiaHttpRequest.GET(baseServiceUrl("user"), builder.build());
        return executeRequest(requestUser, userFunc);

    }

    @RequestMapping(value = "/child/name", method = RequestMethod.POST)
    public ResponseMessage updateChildByName(@RequestParam String utoken,@RequestParam long id, @RequestParam String name) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(name)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("id", id)
                .add("name", name);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("participant/name"),builder.build());
        if(executeRequest(request).getErrno() == 0) {
            MomiaHttpParamBuilder builderUser = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest requestUser = MomiaHttpRequest.GET(baseServiceUrl("user"), builderUser.build());
            return executeRequest(requestUser, userFunc);
        }
        return executeRequest(request);


    }


    @RequestMapping(value = "/child/sex", method = RequestMethod.POST)
    public ResponseMessage updateChildBySex(@RequestParam String utoken,@RequestParam long id, @RequestParam String sex) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(sex)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("id", id)
                .add("sex", sex);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("participant/sex"),builder.build());
        if(executeRequest(request).getErrno() == 0) {
            MomiaHttpParamBuilder builderUser = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest requestUser = MomiaHttpRequest.GET(baseServiceUrl("user"), builderUser.build());
            return executeRequest(requestUser, userFunc);
        }
        return executeRequest(request);

    }

    @RequestMapping(value = "/child/birthday", method = RequestMethod.POST)
    public ResponseMessage updateChildByBirthday(@RequestParam String utoken,@RequestParam long id, @RequestParam String birthday) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(birthday)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("id", id)
                .add("birthday", birthday);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("participant/birthday"),builder.build());
        if(executeRequest(request).getErrno() == 0) {
            MomiaHttpParamBuilder builderUser = new MomiaHttpParamBuilder().add("utoken", utoken);
            MomiaHttpRequest requestUser = MomiaHttpRequest.GET(baseServiceUrl("user"), builderUser.build());
            return executeRequest(requestUser, userFunc);
        }
        return executeRequest(request);

    }
    @RequestMapping(value = "/child/delete", method = RequestMethod.POST)
    public ResponseMessage deleteChild(@RequestParam String utoken, @RequestParam(value = "cid") long childId) {
        if(StringUtils.isBlank(utoken) || childId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(baseServiceUrl("user/child", childId), builder.build());
        if(executeRequest(request).getErrno() == 0) {
            MomiaHttpRequest requestUser = MomiaHttpRequest.GET(baseServiceUrl("user"), builder.build());
            return executeRequest(requestUser, userFunc);
        }
        return executeRequest(request);
    }

    @RequestMapping(value = "/child", method = RequestMethod.GET)
    public ResponseMessage getChild(@RequestParam String utoken, @RequestParam(value = "cid") long childId) {
        if(StringUtils.isBlank(utoken) || childId <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user/child", childId), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/child/list", method = RequestMethod.GET)
    public ResponseMessage getChildren(@RequestParam String utoken) {
        if(StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user/child"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                ListDto children = new ListDto();
                JSONArray childrenJson = (JSONArray) data;
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
        });
    }
}
