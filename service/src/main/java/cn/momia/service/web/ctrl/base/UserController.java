package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ParticipantService participantService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getUser(@PathVariable long id) {
        User user = userService.get(id);

        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");
        return new ResponseMessage(user);
    }

    @RequestMapping(value = "/{id}/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@PathVariable long userId, @RequestParam String name) {
        boolean successful = userService.updateName(userId, name);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user name");
        return new ResponseMessage("update user name successfully");
    }

    @RequestMapping(value = "/{id}/desc", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@PathVariable long userId, @RequestParam String desc) {
        boolean successful = userService.updateDesc(userId, desc);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user desc");
        return new ResponseMessage("update user desc successfully");
    }

    @RequestMapping(value = "/{id}/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@PathVariable long userId, @RequestParam int sex) {
        boolean successful = userService.updateSex(userId, sex);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user sex");
        return new ResponseMessage("update user sex successfully");
    }

    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@PathVariable long userId, @RequestParam String avatar) {
        boolean successful = userService.updateAvatar(userId, avatar);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user avatar");
        return new ResponseMessage("update user avatar successfully");
    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@PathVariable long userId, @RequestParam String address) {
        boolean successful = userService.updateAddress(userId, address);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user address");
        return new ResponseMessage("update user address successfully");
    }

    @RequestMapping(value = "/{id}/idcardno", method = RequestMethod.PUT)
    public ResponseMessage updateIdCardNo(@PathVariable long userId, @RequestParam String idCardNo) {
        boolean successful = userService.updateIdCardNo(userId, idCardNo);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user id card number");
        return new ResponseMessage("update user id card number successfully");
    }

    @RequestMapping(value = "/{id}/idcardpic", method = RequestMethod.PUT)
    public ResponseMessage updateIdCardPic(@PathVariable long userId, @RequestParam String idCardPic) {
        boolean successful = userService.updateIdCardPic(userId, idCardPic);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user id card pic");
        return new ResponseMessage("update user id card pic successfully");
    }

    @RequestMapping(value = "{id}/participant", method = RequestMethod.POST)
    public ResponseMessage addParticipant(@PathVariable long id, @RequestParam String participantJson) throws ParseException {
        Participant participant = new Participant(JSON.parseObject(participantJson));
        participant.setUserId(id);
        long participantId = participantService.add(participant);
        if(participantId <= 0) new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add participant");

        participant.setId(participantId);
        return new ResponseMessage(participant);
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.GET)
    public ResponseMessage getAllParticipants(@PathVariable long id){
        List<Participant> participants = participantService.getByUser(id);

        return new ResponseMessage(participants);
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.GET)
    public ResponseMessage getParticipant(@PathVariable(value = "id") long userId, @RequestParam(value = "pid") long participantId){
        Participant participant = participantService.get(participantId, userId);
        if (!participant.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "participant not exists");

        return new ResponseMessage(participant);
    }

    @RequestMapping(value = "/{id}/participant/{pid}/name", method = RequestMethod.PUT)
    public ResponseMessage updateParticipantName(@PathVariable(value = "id") long userId, @RequestParam(value = "pid") long participantId, @RequestParam String name) {
        boolean successful = participantService.updateName(participantId, userId, name);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update participant name");
        return new ResponseMessage("update participant name successfully");
    }

    @RequestMapping(value = "/{id}/participant/{pid}/sex", method = RequestMethod.PUT)
    public ResponseMessage updateParticipantSex(@PathVariable(value = "id") long userId, @RequestParam(value = "pid") long participantId, @RequestParam int sex) {
        boolean successful = participantService.updateSex(participantId, userId, sex);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update participant sex");
        return new ResponseMessage("update participant sex successfully");
    }

    @RequestMapping(value = "/{id}/participant/{pid}/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateParticipantBirthday(@PathVariable(value = "id") long userId, @RequestParam(value = "pid") long participantId, @RequestParam String birthday) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
            boolean successful = participantService.updateBirthday(participantId, userId, date);
            if (successful) return new ResponseMessage("update participant name successfully");
        } catch (ParseException e) {
            LOGGER.error("fail to update participant name", e);
        }

        return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update participant name");
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteParticipant(@PathVariable(value = "id") long userId, @RequestParam(value = "pid") long participantId){
        boolean successful = participantService.delete(participantId, userId);
        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to delete participant");

        return new ResponseMessage("delete participant successfully");
    }

}
