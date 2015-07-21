package cn.momia.service.web.ctrl.user;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends UserRelatedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile, @RequestParam String type) {
        if (!commonServiceFacade.sendCode(mobile, type)) return ResponseMessage.FAILED("发送短信验证码失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMessage register(@RequestParam(value = "nickname") String nickName,
                                    @RequestParam String mobile,
                                    @RequestParam String password,
                                    @RequestParam String code){
        if (!commonServiceFacade.verifyCode(mobile, code)) return ResponseMessage.FAILED("验证码不正确");

        if (userServiceFacade.exists("mobile", mobile)) return ResponseMessage.FAILED("该手机号已经注册过");
        if (userServiceFacade.exists("nickName", nickName)) return ResponseMessage.FAILED("该昵称已存在");

        User user = userServiceFacade.register(nickName, mobile, password);
        if (!user.exists()) return ResponseMessage.FAILED("注册失败");

        long userCouponId = promoServiceFacade.getUserRegisterCoupon(user.getId());

        return new ResponseMessage(buildUserResponse(user, userCouponId));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam String password) {
        LOGGER.info("login by password: {}", mobile);

        User user = userServiceFacade.login(mobile, password);
        if (!user.exists()) return ResponseMessage.FAILED("登录失败，密码不正确");

        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/login/code", method = RequestMethod.POST)
    public ResponseMessage loginByCode(@RequestParam String mobile, @RequestParam String code) {
        LOGGER.info("login by code: {}", mobile);

        if (!commonServiceFacade.verifyCode(mobile, code)) return ResponseMessage.FAILED("验证码不正确");

        User user = userServiceFacade.getUserByMobile(mobile);
        if (!user.exists()) return ResponseMessage.FAILED("登录失败");

        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public ResponseMessage updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        if (!commonServiceFacade.verifyCode(mobile, code)) return ResponseMessage.FAILED("验证码不正确");

        User user = userServiceFacade.updateUserPassword(mobile, password);
        if (!user.exists()) return ResponseMessage.FAILED("更改密码失败");

        return new ResponseMessage(buildUserResponse(user));
    }
}
