package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/participant")
public class ParticipantV1Api extends AbstractV1Api {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addParticipant(@RequestParam String utoken, @RequestParam String participant) {
        if (StringUtils.isBlank(utoken) || StringUtils.isBlank(participant)) return ResponseMessage.BAD_REQUEST;

        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", getUserId(utoken));
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("participant"), paticipantJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam String utoken, @RequestParam long id) {
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant", id), builder.build());

        return executeRequest(request, userFunc);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseMessage updateParticipant(@RequestParam String utoken, @RequestParam String participant) {
        if (StringUtils.isBlank(utoken) || StringUtils.isBlank(participant)) return ResponseMessage.BAD_REQUEST;

        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", getUserId(utoken));
        MomiaHttpRequest request = MomiaHttpRequest.PUT(url("participant"), paticipantJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteParticipant(@RequestParam String utoken, @RequestParam long id) {
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(url("participant", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(url("participant"), builder.build());

        return executeRequest(request, userFunc);
    }
}
