package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.Dto;
import cn.momia.mapi.api.v1.dto.ParticipantDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/participant")
public class ParticipantApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addParticipant(@RequestParam String utoken, @RequestParam String participant) {
        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", getUserId(utoken));
        MomiaHttpRequest request = MomiaHttpRequest.POST(baseServiceUrl("participant"), paticipantJson.toString());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new ParticipantDto((JSONObject) data);
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new ParticipantDto((JSONObject) data);
            }
        });
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseMessage updateParticipant(@RequestParam String utoken, @RequestParam String participant) {
        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", getUserId(utoken));
        MomiaHttpRequest request = MomiaHttpRequest.PUT(baseServiceUrl("participant"), paticipantJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteParticipant(@RequestParam long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.DELETE(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("participant"), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                ParticipantDto.Participants participants = new ParticipantDto.Participants();
                JSONArray participantsArray = (JSONArray) data;
                for (int i = 0; i < participantsArray.size(); i++) {
                    JSONObject participantJson = participantsArray.getJSONObject(i);
                    participants.add(new ParticipantDto(participantJson));
                }

                return participants;
            }
        });
    }
}
