package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpDeleteRequest;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.http.impl.MomiaHttpPutRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseMessage updateBirthday(@RequestParam String utoken, @RequestParam String birthday) {
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

    @RequestMapping(value = "/children", method = RequestMethod.PUT)
    public ResponseMessage updateChildren(@RequestParam String utoken, @RequestParam int children) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("children", children);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/children"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/participant", method = RequestMethod.GET)
    public ResponseMessage getParticipants(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user/participant"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/participant", method = RequestMethod.POST)
    public ResponseMessage addParticipant(@RequestParam String utoken, @RequestParam(value = "participant") String participantJson) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("participant", participantJson);
        MomiaHttpRequest request = new MomiaHttpPostRequest(baseServiceUrl("user/participant"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/participant/{pid}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam String utoken, @PathVariable long pid) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user/participant", pid), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/participant/{pid}", method = RequestMethod.PUT)
    public ResponseMessage updateParticipant(@RequestParam String utoken, @PathVariable long pid, @RequestParam(value = "participant") String participantJson) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("participant", participantJson);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user/participant", pid), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/participant/{pid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@RequestParam String utoken, @PathVariable long pid) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(baseServiceUrl("user/participant", pid), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public ResponseMessage getFavorites(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user/favorite"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/favorite/{fid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteFavorite(@RequestParam String utoken, @PathVariable long fid) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(baseServiceUrl("user/favorite", fid), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrders(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user/order"), builder.build());

        return executeRequest(request);
    }
}
