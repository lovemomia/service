package cn.momia.service.user.web.ctrl;

import cn.momia.service.common.api.CommonServiceApi;
import cn.momia.service.base.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends UserRelatedController {
    @Autowired private CommonServiceApi commonServiceApi;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMessage register(@RequestParam(value = "nickname") String nickName,
                                    @RequestParam String mobile,
                                    @RequestParam String password,
                                    @RequestParam String code){
        if (userServiceFacade.exists("mobile", mobile)) return ResponseMessage.FAILED("该手机号已经注册过");
        if (userServiceFacade.exists("nickName", nickName)) return ResponseMessage.FAILED("该昵称已存在");

        commonServiceApi.SMS.verify(mobile, code);

        User user = userServiceFacade.register(nickName, mobile, password);
        if (!user.exists()) return ResponseMessage.FAILED("注册失败");

        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam String password) {
        User user = userServiceFacade.login(mobile, password);
        if (!user.exists()) return ResponseMessage.FAILED("登录失败，密码不正确");

        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/login/code", method = RequestMethod.POST)
    public ResponseMessage loginByCode(@RequestParam String mobile, @RequestParam String code) {
        commonServiceApi.SMS.verify(mobile, code);

        User user = userServiceFacade.getUserByMobile(mobile);
        if (!user.exists()) return ResponseMessage.FAILED("登录失败");

        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public ResponseMessage updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        commonServiceApi.SMS.verify(mobile, code);

        User user = userServiceFacade.updateUserPassword(mobile, password);
        if (!user.exists()) return ResponseMessage.FAILED("更改密码失败");

        return ResponseMessage.SUCCESS(buildUserResponse(user));
    }
}
