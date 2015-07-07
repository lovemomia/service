package cn.momia.service.web.ctrl.base;

import cn.momia.common.misc.ValidateUtil;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.ParticipantService;
import cn.momia.service.sms.SmsSender;
import cn.momia.service.sms.SmsVerifier;
import cn.momia.service.sms.impl.SmsLoginException;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired private SmsSender smsSender;
    @Autowired private SmsVerifier smsVerifier;

    @Autowired private UserService userService;
    @Autowired private ParticipantService participantService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile, @RequestParam String type) {
        if (ValidateUtil.isInvalidMobile(mobile) || ValidateUtil.notIn(type, "login", "register")) return ResponseMessage.BAD_REQUEST;

        try {
            smsSender.send(mobile, type);
            return ResponseMessage.SUCCESS;
        }
        catch (SmsLoginException e) {
           return new ResponseMessage(ErrorCode.NOT_REGISTERED, e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("fail to send verify code for {}", mobile, e);
            return ResponseMessage.FAILED("发送验证码失败");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam String password) {
        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(password)) return ResponseMessage.BAD_REQUEST;


        User user = userService.getByMobile(mobile);
        String token = generateToken(mobile);
        if (!user.exists()) {
                LOGGER.error("fail to login user for {}", mobile);
                return new ResponseMessage(ErrorCode.NOT_REGISTERED, "登录失败，用户不存在，请先注册");
        } else {
            if(!StringUtils.equals(user.getPassword(), password))
                return new ResponseMessage(ErrorCode.WRONG_PASSWORD, "登录失败，密码错误");
            if (!userService.updateToken(user.getId(), token)) {
                LOGGER.warn("fail to update token for {}, will use old token", mobile);
            } else {
                user.setToken(token);
            }
        }

        return new ResponseMessage(buildUserResponse(user));
    }

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, new Date().toString(), SecretKey.get() }, "|"));
    }

    private JSONObject buildUserResponse(User user) {
        JSONObject userPackJson = new JSONObject();
        userPackJson.put("user", user);
        userPackJson.put("children", participantService.get(user.getChildren()).values());

        return userPackJson;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMessage register(@RequestParam String nickName, @RequestParam String mobile, @RequestParam String password, @RequestParam String code){
        if (StringUtils.isBlank(nickName) || ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(password) || StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;

        if(userService.getByNickName(nickName).exists()) return ResponseMessage.FAILED("注册失败，用户昵称已存在");

        if (!smsVerifier.verify(mobile, code)) return ResponseMessage.FAILED("验证码不正确");

        User user = userService.getByMobile(mobile);
        String token = generateToken(mobile);
        if (!user.exists()) {
            user = userService.add(nickName, mobile, password, token);
            if (!user.exists()) {
                LOGGER.error("fail to register user for {}", mobile);
                return ResponseMessage.FAILED("注册失败");
            }
        } else {
            return ResponseMessage.FAILED("注册失败，用户已存在");
        }

        return new ResponseMessage(buildUserResponse(user));
    }
}
