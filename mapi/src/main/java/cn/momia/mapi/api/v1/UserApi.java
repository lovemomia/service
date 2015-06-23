package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.http.impl.MomiaHttpPutRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.Dto;
import cn.momia.mapi.api.v1.dto.UserDto;
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
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id));

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                JSONObject userJson = (JSONObject) data;
                UserDto.Other other = new UserDto.Other();
                other.setMobile(userJson.getString("mobile"));
                other.setAvatar(userJson.getString("avatar"));
                other.setName(userJson.getString("name"));
                other.setSex(userJson.getString("sex"));
                other.setBirthday(userJson.getDate("birthday"));
                other.setCityId(userJson.getInteger("cityId"));
                other.setAddress(userJson.getString("address"));

                return other;
            }
        });
    }

    @RequestMapping(value = "/view/order", method = RequestMethod.GET)
    public ResponseMessage viewOrders(@RequestParam long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id, "order"));

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                JSONObject userJson = (JSONObject) data;
                UserDto.Own own = new UserDto.Own();
                own.setToken(userJson.getString("token"));
                own.setMobile(userJson.getString("mobile"));
                own.setAvatar(userJson.getString("avatar"));
                own.setName(userJson.getString("name"));
                own.setSex(userJson.getString("sex"));
                own.setBirthday(userJson.getDate("birthday"));
                own.setCityId(userJson.getInteger("cityId"));
                own.setAddress(userJson.getString("address"));

                return own;
            }
        });
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/avatar"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/name", method = RequestMethod.POST)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/name"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.POST)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/sex"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.POST)
    public ResponseMessage updateBirthday(@RequestParam String utoken, @RequestParam Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", birthday);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/birthday"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/city", method = RequestMethod.POST)
    public ResponseMessage updateCity(@RequestParam String utoken, @RequestParam int city) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", city);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/city"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/address"), builder.build());

        return executeRequest(request);
    }
}
