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
    public ResponseMessage getUser(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id));

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@PathVariable long id, @RequestParam String avatar) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("avatar", avatar);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "avatar"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@PathVariable long id, @RequestParam String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("name", name);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "name"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@PathVariable long id, @RequestParam int sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("sex", sex);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "sex"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateBirthday(@PathVariable long id, @RequestParam String birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("birthday", birthday);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "birthday"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/city", method = RequestMethod.PUT)
    public ResponseMessage updateCity(@PathVariable long id, @RequestParam int city) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("city", city);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "city"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@PathVariable long id, @RequestParam String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("address", address);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "address"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/children", method = RequestMethod.PUT)
    public ResponseMessage updateChildren(@PathVariable long id, @RequestParam int children) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("children", children);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "children"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.GET)
    public ResponseMessage getParticipants(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id, "participant"));

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.POST)
    public ResponseMessage addParticipant(@PathVariable long id, @RequestParam(value = "participant") String participantJson) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("participant", participantJson);
        MomiaHttpRequest request = new MomiaHttpPostRequest(baseServiceUrl("user", id, "participant"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@PathVariable long id, @PathVariable long pid) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id, "participant", pid));

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.PUT)
    public ResponseMessage updateParticipant(@PathVariable long id, @PathVariable long pid, @RequestParam(value = "participant") String participantJson) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("participant", participantJson);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("user", id, "participant", pid), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@PathVariable long id, @PathVariable long pid) {
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(baseServiceUrl("user", id, "participant", pid));

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/favorite", method = RequestMethod.GET)
    public ResponseMessage getFavorites(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id, "favorite"));

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/favorite/{fid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteFavorite(@PathVariable long id, @PathVariable long fid) {
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(baseServiceUrl("user", id, "favorite", fid));

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/order", method = RequestMethod.GET)
    public ResponseMessage getOrders(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user", id, "order"));

        return executeRequest(request);
    }
}
