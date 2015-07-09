package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    @Autowired private ParticipantService participantService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addParticipant(@RequestBody Participant participant) {
        if (participant.isInvalid()) return ResponseMessage.BAD_REQUEST;

        long participantId = participantService.add(participant);

        if(participantId <= 0) return ResponseMessage.FAILED("添加出行人失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@RequestParam String utoken, @PathVariable long id){
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Participant participant = participantService.get(id);
        if (!participant.exists() || participant.getUserId() != user.getId()) return ResponseMessage.BAD_REQUEST;

        return new ResponseMessage(participant);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public ResponseMessage updateParticipantName(@RequestBody Participant participant) {
        if (participant.isInvalid()) return ResponseMessage.BAD_REQUEST;

        boolean successful = participantService.update(participant);

        if (!successful) return ResponseMessage.FAILED("更新出行人失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/name",method = RequestMethod.PUT)
    public ResponseMessage updateParticipantByName(@RequestParam String utoken,
                                                   @RequestParam long id,
                                                   @RequestParam String name) {
        if (StringUtils.isBlank(utoken) || id <= 0 || StringUtils.isBlank(name)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = participantService.updateByName(id, name, user.getId());

        if (!successful) return ResponseMessage.FAILED("更新姓名失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/sex",method = RequestMethod.PUT)
    public ResponseMessage updateParticipantBySex(@RequestParam String utoken,
                                                  @RequestParam long id,
                                                  @RequestParam String sex) {
        if (StringUtils.isBlank(utoken) || id <= 0 || StringUtils.isBlank(sex)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = participantService.updateBySex(id, sex, user.getId());

        if (!successful) return ResponseMessage.FAILED("更新性别失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/birthday",method = RequestMethod.PUT)
    public ResponseMessage updateParticipantByBirthday(@RequestParam String utoken,
                                                       @RequestParam long id,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date birthday) {
        if (StringUtils.isBlank(utoken) || id <= 0 || birthday == null) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = participantService.updateByBirthday(id, birthday, user.getId());

        if (!successful) return ResponseMessage.FAILED("更新生日失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@RequestParam String utoken, @PathVariable long id){
        if (StringUtils.isBlank(utoken) || id <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = participantService.delete(id, user.getId());

        if (!successful) return ResponseMessage.FAILED("删除出行人失败");
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
