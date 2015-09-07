package cn.momia.service.user.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.user.web.ctrl.dto.ParticipantDto;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import com.google.common.base.Splitter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/participant")
public class ParticipantController extends UserRelatedController {
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Participant participant) {
        if(!userServiceFacade.addParticipant(participant)) return MomiaHttpResponse.FAILED("添加出行人失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken, @PathVariable long id){
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Participant participant = userServiceFacade.getParticipant(user.getId(), id);
        if (!participant.exists()) return MomiaHttpResponse.FAILED("出行人不存在");

        return MomiaHttpResponse.SUCCESS(new ParticipantDto(participant, true));
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public MomiaHttpResponse update(@RequestBody Participant participant) {
        if (!userServiceFacade.updateParticipant(participant)) return MomiaHttpResponse.FAILED("更新出行人失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam String utoken, @PathVariable long id){
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.deleteParticipant(user.getId(), id);
        if (!successful) return MomiaHttpResponse.FAILED("删除出行人失败");

        Set<Long> children = user.getChildren();
        if (children.contains(id)) children.remove(id);
        userServiceFacade.updateUserChildren(user.getId(), children);

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse listByUser(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(buildParticipantsResponse(userServiceFacade.getParticipantsByUser(user.getId())));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "paids") String paids) {
        List<Long> ids = new ArrayList<Long>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(paids)) ids.add(Long.valueOf(id));
        List<Participant> participants = userServiceFacade.getParticipants(ids);

        return MomiaHttpResponse.SUCCESS(ParticipantDto.toDtos(participants));
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public MomiaHttpResponse check(@RequestParam(value = "uid") long userId, @RequestParam(value = "paids") String paids) {
        List<Long> ids = new ArrayList<Long>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(paids)) ids.add(Long.valueOf(id));

        if (!userServiceFacade.checkParticipants(userId, ids)) return MomiaHttpResponse.FAILED("出行人信息不正确");
        return MomiaHttpResponse.SUCCESS;
    }
}
