package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/participant")
public class ParticipantController {
    @Autowired private ParticipantService participantService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addParticipant(@RequestBody Participant participant) {
        long participantId = participantService.add(participant);
        if(participantId <= 0) return ResponseMessage.FAILED("fail to add participant");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam String utoken, @PathVariable long id){
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Participant participant = participantService.get(id);
        if (!participant.exists() || participant.getUserId() != user.getId()) return ResponseMessage.FAILED("participant not exists");

        return new ResponseMessage(participant);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public ResponseMessage updateParticipantName(@RequestBody Participant participant) {
        boolean successful = participantService.update(participant);

        if (!successful) return ResponseMessage.FAILED("fail to update participant");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@RequestParam String utoken, @PathVariable long id){
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = participantService.delete(id, user.getId());
        if (!successful) return ResponseMessage.FAILED("fail to delete participant");

        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken){
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        List<Participant> participants = participantService.getByUser(user.getId());

        return new ResponseMessage(participants);
    }
}
