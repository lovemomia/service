package cn.momia.service.user.web.ctrl;

import cn.momia.api.base.SmsServiceApi;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.user.base.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends UserRelatedController {
    @Autowired private SmsServiceApi smsServiceApi;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public MomiaHttpResponse register(@RequestParam(value = "nickname") String nickName,
                                      @RequestParam String mobile,
                                      @RequestParam String password,
                                      @RequestParam String code){
        if (userService.exists("nickName", nickName)) return MomiaHttpResponse.FAILED("昵称已存在，不能使用");
        if (userService.exists("mobile", mobile)) return MomiaHttpResponse.FAILED("手机号已经注册过");

        if (!smsServiceApi.verify(mobile, code)) return MomiaHttpResponse.FAILED("验证码不正确");

        long userId = userService.add(nickName, mobile, password);
        if (userId <= 0) return MomiaHttpResponse.FAILED("注册失败");

        return MomiaHttpResponse.SUCCESS(buildUserDto(userService.get(userId), User.Type.FULL));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public MomiaHttpResponse login(@RequestParam String mobile, @RequestParam String password) {
        User user = userService.getByMobile(mobile);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在，请先注册");
        if (!userService.validatePassword(mobile, password)) return MomiaHttpResponse.FAILED("登录失败，密码不正确");

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/login/code", method = RequestMethod.POST)
    public MomiaHttpResponse loginByCode(@RequestParam String mobile, @RequestParam String code) {
        if (!smsServiceApi.verify(mobile, code)) return MomiaHttpResponse.FAILED("验证码不正确");

        User user = userService.getByMobile(mobile);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在，请先注册");

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public MomiaHttpResponse updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        if (!smsServiceApi.verify(mobile, code)) return MomiaHttpResponse.FAILED("验证码不正确");

        User user = userService.getByMobile(mobile);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在，请先注册");
        if (!userService.updatePassword(user.getId(), mobile, password)) return MomiaHttpResponse.FAILED("更改密码失败");

        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL));
    }
}
