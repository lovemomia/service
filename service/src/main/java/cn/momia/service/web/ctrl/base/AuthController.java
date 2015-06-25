package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.sms.SmsSender;
import cn.momia.service.sms.SmsVerifier;
import cn.momia.service.web.ctrl.AbstractController;
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

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private SmsVerifier smsVerifier;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile) {
        try {
            smsSender.send(mobile);
            return ResponseMessage.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("fail to send verify code for {}", mobile, e);
            return new ResponseMessage(ErrorCode.FAILED, "fail to send verify code");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam String code) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code))
            return new ResponseMessage(ErrorCode.FAILED, "mobile or(and) verify code is empty");

        if (!smsVerifier.verify(mobile, code)) return new ResponseMessage(ErrorCode.FAILED, "fail to verify code");

        User user = userService.getByMobile(mobile);
        String token = generateToken(mobile);
        if (!user.exists()) {
            user = userService.add(mobile, token);
            if (!user.exists()) {
                LOGGER.error("fail to add user for {}", mobile);
                return new ResponseMessage(ErrorCode.FAILED, "fail to login");
            }
        } else {
            if (!userService.updateToken(user.getId(), token)) {
                LOGGER.warn("fail to update token for {}, will use old token", mobile);
            } else {
                user.setToken(token);
            }
        }

        return new ResponseMessage(user);
    }

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, new Date().toString(), SecretKey.get() }, "|"));
    }
}
