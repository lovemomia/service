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

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getAllParticipants(@RequestParam String utoken){
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        List<Participant> participants = participantService.getByUser(user.getId());

        return new ResponseMessage(participants);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@PathVariable long id){
        Participant participant = participantService.get(id);
        if (!participant.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "participant not exists");

        return new ResponseMessage(participant);
    }

    @RequestMapping(value = "/{id}/name", method = RequestMethod.PUT)
    public ResponseMessage updateParticipantName(@PathVariable long id, @RequestParam String utoken, @RequestParam String name) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = participantService.updateName(id, user.getId(), name);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update participant name");
        return new ResponseMessage("update participant name successfully");
    }

    @RequestMapping(value = "/{id}/sex", method = RequestMethod.PUT)
    public ResponseMessage updateParticipantSex(@PathVariable long id, @RequestParam String utoken, @RequestParam int sex) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = participantService.updateSex(id, user.getId(), sex);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update participant sex");
        return new ResponseMessage("update participant sex successfully");
    }

    @RequestMapping(value = "/{id}/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateParticipantBirthday(@PathVariable long id, @RequestParam String utoken, @RequestParam Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = participantService.updateBirthday(id, user.getId(), birthday);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update participant birthday");
        return new ResponseMessage("update participant birthday successfully");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@PathVariable long id, @RequestParam String utoken){
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = participantService.delete(id, user.getId());
        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to delete participant");

        return new ResponseMessage("delete participant successfully");
    }
}
