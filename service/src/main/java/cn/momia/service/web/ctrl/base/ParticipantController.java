package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/participant")
public class ParticipantController {
    @Autowired
    private UserService userService;

    @Autowired
    private ParticipantService participantService;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addParticipant(@RequestBody Participant participant) {
        long participantId = participantService.add(participant);
        if(participantId <= 0) new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add participant");

        participant.setId(participantId);
        return new ResponseMessage(participant);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@PathVariable long id){
        Participant participant = participantService.get(id);
        if (!participant.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "participant not exists");

        return new ResponseMessage(participant);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseMessage updateParticipantName(@RequestBody Participant participant) {
        boolean successful = participantService.update(participant);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update participant name");
        return new ResponseMessage("update participant name successfully");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@PathVariable long id, @RequestParam String utoken){
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = participantService.delete(id, user.getId());
        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to delete participant");

        return new ResponseMessage("delete participant successfully");
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getParticipantsOfUser(@RequestParam String utoken){
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        List<Participant> participants = participantService.getByUser(user.getId());

        return new ResponseMessage(participants);
    }
}
