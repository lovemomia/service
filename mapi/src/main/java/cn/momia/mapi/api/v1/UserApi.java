package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.http.impl.MomiaHttpPutRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/v1/user")
public class UserApi extends AbstractApi {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage viewUser(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id));

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/order", method = RequestMethod.GET)
    public ResponseMessage viewOrders(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id, "order"));

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("avatar", avatar);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/avatar"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/name"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam int sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/sex"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateBirthday(@RequestParam String utoken, @RequestParam Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", birthday);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/birthday"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public ResponseMessage updateCity(@RequestParam String utoken, @RequestParam int city) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("city", city);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/city"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/address"), builder.build());

        return executeRequest(request);
    }
}
