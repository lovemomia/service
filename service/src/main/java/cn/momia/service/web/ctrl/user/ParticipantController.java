package cn.momia.service.web.ctrl.user;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/participant")
public class ParticipantController extends AbstractController {
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addParticipant(@RequestBody Participant participant) {
        long participantId = userServiceFacade.addParticipant(participant);
        if(participantId <= 0) return ResponseMessage.FAILED("添加出行人失败");

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam String utoken, @PathVariable long id){
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Participant participant = userServiceFacade.getParticipant(user.getId(), id);
        if (!participant.exists()) return ResponseMessage.FAILED("出行人不存在");

        return new ResponseMessage(participant);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public ResponseMessage updateParticipantName(@RequestBody Participant participant) {
        boolean successful = userServiceFacade.updateParticipant(participant);
        if (!successful) return ResponseMessage.FAILED("更新出行人失败");

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@RequestParam String utoken, @PathVariable long id){
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.deleteParticipant(user.getId(), id);
        if (!successful) return ResponseMessage.FAILED("删除出行人失败");

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken){
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        return new ResponseMessage(userServiceFacade.getParticipantsByUser(user.getId()));
    }
}
