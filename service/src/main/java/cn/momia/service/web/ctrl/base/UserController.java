package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cn/momia/service/base/user/{userId}")
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage get(@PathVariable long userId) {
        User user = userService.get(userId);

        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "cn.momia.service.base.user: " + userId + " not exists");
        return new ResponseMessage(user);
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@PathVariable long userId, @RequestParam String name) {
        boolean successful = userService.updateName(userId, name);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update name of cn.momia.service.base.user: " + userId);
        return new ResponseMessage("update name of cn.momia.service.base.user successfully");
    }

    @RequestMapping(value = "/desc", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@PathVariable long userId, @RequestParam String desc) {
        boolean successful = userService.updateDesc(userId, desc);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update desc of cn.momia.service.base.user: " + userId);
        return new ResponseMessage("update desc of cn.momia.service.base.user successfully");
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@PathVariable long userId, @RequestParam int sex) {
        boolean successful = userService.updateSex(userId, sex);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update sex of cn.momia.service.base.user: " + userId);
        return new ResponseMessage("update sex of cn.momia.service.base.user successfully");
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@PathVariable long userId, @RequestParam String avatar) {
        boolean successful = userService.updateAvatar(userId, avatar);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update avatar of cn.momia.service.base.user: " + userId);
        return new ResponseMessage("update avatar of cn.momia.service.base.user successfully");
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@PathVariable long userId, @RequestParam String address) {
        boolean successful = userService.updateAddress(userId, address);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update address of cn.momia.service.base.user: " + userId);
        return new ResponseMessage("update address of cn.momia.service.base.user successfully");
    }

    @RequestMapping(value = "/idcardno", method = RequestMethod.PUT)
    public ResponseMessage updateIdCardNo(@PathVariable long userId, @RequestParam String idCardNo) {
        boolean successful = userService.updateIdCardNo(userId, idCardNo);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update id card number of cn.momia.service.base.user: " + userId);
        return new ResponseMessage("update id card number of cn.momia.service.base.user successfully");
    }

    @RequestMapping(value = "/idcardpic", method = RequestMethod.PUT)
    public ResponseMessage updateIdCardPic(@PathVariable long userId, @RequestParam String idCardPic) {
        boolean successful = userService.updateIdCardPic(userId, idCardPic);

        if (!successful)
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update id card pic of cn.momia.service.base.user: " + userId);
        return new ResponseMessage("update id card pic of cn.momia.service.base.user successfully");
    }
}
