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
@RequestMapping("/participant")
public class ParticipantApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addParticipant(@RequestParam String utoken, @RequestParam String participant) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("participant", participant);
        MomiaHttpRequest request = new MomiaHttpPostRequest(baseServiceUrl("participant"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@PathVariable long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage updateParticipant(@PathVariable long id, @RequestParam String utoken, @RequestParam String participant) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("participant", participant);
        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@PathVariable long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("participant"), builder.build());

        return executeRequest(request);
    }
}
