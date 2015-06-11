package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController {
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

    @RequestMapping(value = "{id}/participant", method = RequestMethod.PUT)
    public ResponseMessage addParticipant(@PathVariable long userId,@RequestParam Participant participant){
        long successful = participantService.add(userId,participant);

        return new ResponseMessage("add participant successfully");
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.GET)
    public ResponseMessage getAllParticipant(@PathVariable long id){
        List<Participant> participants = participantService.get(id);
        List<ResponseMessage> messages = new ArrayList<ResponseMessage>();
        if(participants.size()==0)
            return new ResponseMessage(ErrorCode.NOT_FOUND, "participant not exists");
        for(Participant participant : participants){
            messages.add(new ResponseMessage(participant));
        }
        return new ResponseMessage(messages);

    }
    

}
