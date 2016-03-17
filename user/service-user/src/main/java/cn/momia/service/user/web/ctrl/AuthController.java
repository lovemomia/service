package cn.momia.service.user.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
    @Autowired private SmsService smsService;
    @Autowired private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public MomiaHttpResponse register(@RequestParam(value = "nickname") String nickName,
                                      @RequestParam String mobile,
                                      @RequestParam String password,
                                      @RequestParam String code){
        if (userService.exists("nickName", nickName)) return MomiaHttpResponse.FAILED("昵称已存在，不能使用");
        if (userService.exists("mobile", mobile)) return MomiaHttpResponse.FAILED("手机号已经注册过");
        if (!smsService.verifyCode(mobile, code)) return MomiaHttpResponse.FAILED("验证码不正确");

        long userId = userService.add(nickName, mobile, password);
        if (userId <= 0) return MomiaHttpResponse.FAILED("注册失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(userId)));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public MomiaHttpResponse login(@RequestParam String mobile, @RequestParam String password) {
        User user = userService.getByMobile(mobile);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在，请先注册");
        if (!userService.validatePassword(mobile, password)) return MomiaHttpResponse.FAILED("登录失败，密码不正确");

        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public MomiaHttpResponse updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        if (!smsService.verifyCode(mobile, code)) return MomiaHttpResponse.FAILED("验证码不正确");

        User user = userService.getByMobile(mobile);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在，请先注册");
        if (!userService.updatePassword(user.getId(), mobile, password)) return MomiaHttpResponse.FAILED("更改密码失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }
}
