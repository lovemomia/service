package cn.momia.mapi.api.v1;

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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserApi extends AbstractApi {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getUser(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest("user", true, baseServiceUrl("user", id), null);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@PathVariable long id, @RequestParam String avatar) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("avatar", avatar);
        MomiaHttpRequest request = new MomiaHttpPutRequest("avatar", true, baseServiceUrl("user", id, "avatar"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@PathVariable long id, @RequestParam String name) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        MomiaHttpRequest request = new MomiaHttpPutRequest("name", true, baseServiceUrl("user", id, "name"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@PathVariable long id, @RequestParam String sex) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sex", sex);
        MomiaHttpRequest request = new MomiaHttpPutRequest("sex", true, baseServiceUrl("user", id, "sex"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateBirthday(@PathVariable long id, @RequestParam String birthday) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("birthday", birthday);
        MomiaHttpRequest request = new MomiaHttpPutRequest("birthday", true, baseServiceUrl("user", id, "birthday"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/city", method = RequestMethod.PUT)
    public ResponseMessage updateCity(@PathVariable long id, @RequestParam String city) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("city", city);
        MomiaHttpRequest request = new MomiaHttpPutRequest("city", true, baseServiceUrl("user", id, "city"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@PathVariable long id, @RequestParam String address) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("address", address);
        MomiaHttpRequest request = new MomiaHttpPutRequest("address", true, baseServiceUrl("user", id, "address"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/children", method = RequestMethod.PUT)
    public ResponseMessage updateChildren(@PathVariable long id, @RequestParam String children) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("children", children);
        MomiaHttpRequest request = new MomiaHttpPutRequest("children", true, baseServiceUrl("user", id, "children"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.GET)
    public ResponseMessage getParticipants(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest("participants", true, baseServiceUrl("user", id, "participant"), null);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.POST)
    public ResponseMessage addParticipant(@PathVariable long id, @RequestParam(value = "participant") String participantJson) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("participant", participantJson);
        MomiaHttpRequest request = new MomiaHttpPostRequest("participant", true, baseServiceUrl("user", id, "participant"), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@PathVariable long id, @PathVariable long pid) {
        MomiaHttpRequest request = new MomiaHttpGetRequest("participant", true, baseServiceUrl("user", id, "participant", pid), null);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.PUT)
    public ResponseMessage updateParticipant(@PathVariable long id, @PathVariable long pid, @RequestParam(value = "participant") String participantJson) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("participant", participantJson);
        MomiaHttpRequest request = new MomiaHttpPutRequest("participant", true, baseServiceUrl("user", id, "participant", pid), params);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@PathVariable long id, @PathVariable long pid) {
        MomiaHttpRequest request = new MomiaHttpDeleteRequest("participant", true, baseServiceUrl("user", id, "participant", pid), null);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/favorite", method = RequestMethod.GET)
    public ResponseMessage getFavorites(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest("favorites", true, baseServiceUrl("user", id, "favorite"), null);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/favorite/{fid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteFavorite(@PathVariable long id, @PathVariable long fid) {
        MomiaHttpRequest request = new MomiaHttpDeleteRequest("favorite", true, baseServiceUrl("user", id, "favorite", fid), null);

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}/order", method = RequestMethod.GET)
    public ResponseMessage getOrders(@PathVariable long id) {
        MomiaHttpRequest request = new MomiaHttpGetRequest("orders", true, baseServiceUrl("user", id, "order"), null);

        return executeRequest(request);
    }
}
