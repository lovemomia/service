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

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getUser(@PathVariable long id) {
        User user = userService.get(id);

        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");
        return new ResponseMessage(user);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);

        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");
        return new ResponseMessage(user);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = userService.updateAvatar(user.getId(), avatar);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user avatar");
        return new ResponseMessage("update user avatar successfully");
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = userService.updateName(user.getId(), name);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user name");
        return new ResponseMessage("update user name successfully");
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam int sex) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = userService.updateSex(user.getId(), sex);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user sex");
        return new ResponseMessage("update user sex successfully");
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = userService.updateBirthday(user.getId(), birthday);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user birthday");
        return new ResponseMessage("update user birthday successfully");
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam int city) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = userService.updateCityId(user.getId(), city);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user city");
        return new ResponseMessage("update user city successfully");
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "user not exists");

        boolean successful = userService.updateAddress(user.getId(), address);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to update user address");
        return new ResponseMessage("update user address successfully");
    }
}
