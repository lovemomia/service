package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.impl.MomiaHttpDeleteRequest;
import cn.momia.common.web.http.impl.MomiaHttpGetRequest;
import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.http.impl.MomiaHttpPutRequest;
import cn.momia.common.web.response.ErrorCode;
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
        ResponseMessage userResponse = getUser(utoken);
        if (userResponse.getErrno() != ErrorCode.SUCCESS) return new ResponseMessage(ErrorCode.FORBIDDEN, userResponse.getErrmsg());

        long userId = ((JSONObject) userResponse.getData()).getLong("id");
        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", userId);

        MomiaHttpRequest request = new MomiaHttpPostRequest(baseServiceUrl("participant"), paticipantJson.toString());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new ParticipantDto((JSONObject) data);
            }
        });
    }

    private ResponseMessage getUser(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("user"), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request, new Function<Object, Dto>() {
            @Override
            public Dto apply(Object data) {
                return new ParticipantDto((JSONObject) data);
            }
        });
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseMessage updateParticipant(@RequestParam String utoken, @RequestParam String participant) {
        ResponseMessage userResponse = getUser(utoken);
        if (userResponse.getErrno() != ErrorCode.SUCCESS) return new ResponseMessage(ErrorCode.FORBIDDEN, userResponse.getErrmsg());

        long userId = ((JSONObject) userResponse.getData()).getLong("id");
        JSONObject paticipantJson = JSON.parseObject(participant);
        paticipantJson.put("userId", userId);

        MomiaHttpRequest request = new MomiaHttpPutRequest(baseServiceUrl("participant"), paticipantJson.toString());

        return executeRequest(request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseMessage deleteParticipant(@RequestParam long id, @RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpDeleteRequest(baseServiceUrl("participant", id), builder.build());

        return executeRequest(request);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = new MomiaHttpGetRequest(baseServiceUrl("participant"), builder.build());

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
