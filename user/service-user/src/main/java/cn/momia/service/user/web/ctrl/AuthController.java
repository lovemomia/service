package cn.momia.service.user.web.ctrl;

import cn.momia.api.base.BaseServiceApi;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController extends UserRelatedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public MomiaHttpResponse register(@RequestParam(value = "nickname") String nickName,
                                      @RequestParam String mobile,
                                      @RequestParam String password,
                                      @RequestParam String code){
        if (userServiceFacade.exists("mobile", mobile)) return MomiaHttpResponse.FAILED("该手机号已经注册过");
        if (userServiceFacade.exists("nickName", nickName)) return MomiaHttpResponse.FAILED("该昵称已存在");

        BaseServiceApi.SMS.verify(mobile, code);

        User user = userServiceFacade.register(nickName, mobile, password);
        if (!user.exists()) return MomiaHttpResponse.FAILED("注册失败");

        try {
            Participant participant = new Participant();
            participant.setUserId(user.getId());
            participant.setName(user.getNickName());
            participant.setSex("女");
            participant.setBirthday(new Date(0));
            userServiceFacade.addParticipant(participant);
        } catch (Exception e) {
            LOGGER.error("fail to add participant for user: {}", user.getId(), e);
        }

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public MomiaHttpResponse login(@RequestParam String mobile, @RequestParam String password) {
        User user = userServiceFacade.getUserByMobile(mobile);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在，请先注册");

        user = userServiceFacade.login(mobile, password);
        if (!user.exists()) return MomiaHttpResponse.FAILED("登录失败，密码不正确");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/login/code", method = RequestMethod.POST)
    public MomiaHttpResponse loginByCode(@RequestParam String mobile, @RequestParam String code) {
        BaseServiceApi.SMS.verify(mobile, code);

        User user = userServiceFacade.getUserByMobile(mobile);
        if (!user.exists()) return MomiaHttpResponse.FAILED("登录失败");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public MomiaHttpResponse updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        BaseServiceApi.SMS.verify(mobile, code);

        User user = userServiceFacade.updateUserPassword(mobile, password);
        if (!user.exists()) return MomiaHttpResponse.FAILED("更改密码失败");

        return MomiaHttpResponse.SUCCESS(buildUserResponse(user));
    }
}
