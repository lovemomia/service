package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ParticipantDto;
import cn.momia.mapi.api.v1.dto.composite.ListDto;
import com.alibaba.fastjson.JSON;
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
@RequestMapping("/v1/participant")
public class ParticipantV1Api extends AbstractV1Api {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantV1Api.class);

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addParticipant(@RequestParam String utoken, @RequestParam String participant) {
        if (StringUtils.isBlank(utoken) || StringUtils.isBlank(participant)) return ResponseMessage.BAD_REQUEST;

        long userId = getUserId(utoken);
        if (userId <= 0) return ResponseMessage.TOKEN_EXPIRED;

        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", userId);
        MomiaHttpRequest request = MomiaHttpRequest.POST(baseServiceUrl("participant"), paticipantJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam String utoken, @RequestParam long id) {
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new ParticipantDto((JSONObject) data, true);
            }
        });
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseMessage updateParticipant(@RequestParam String utoken, @RequestParam String participant) {
        if (StringUtils.isBlank(utoken) || StringUtils.isBlank(participant)) return ResponseMessage.BAD_REQUEST;

        long userId = getUserId(utoken);
        if (userId <= 0) return ResponseMessage.TOKEN_EXPIRED;

        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", userId);
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("participant"), paticipantJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteParticipant(@RequestParam String utoken, @RequestParam long id) {
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("participant"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return buildParticipantsDto((JSONArray) data);
            }
        });
    }

    private Dto buildParticipantsDto(JSONArray participantsJson) {
        ListDto participants = new ListDto();
        for (int i = 0; i < participantsJson.size(); i++) {
            try {
                JSONObject participantJson = participantsJson.getJSONObject(i);
                participants.add(new ParticipantDto(participantJson));
            } catch (Exception e) {
                LOGGER.error("invalid participant: {}", participantsJson.get(i));
            }
        }

        return participants;
    }
}
