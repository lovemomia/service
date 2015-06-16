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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends AbstractController {
    @Autowired
    private SmsSender smsSender;

    @Autowired
    private SmsVerifier smsVerifier;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile) {
        boolean successful = smsSender.send(mobile);

        if (!successful) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to send verify code");
        return new ResponseMessage("send verify code successfully");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam(value = "code") String verifyCode) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(verifyCode))
            return new ResponseMessage(ErrorCode.BAD_REQUEST, "mobile or(and) verify code is empty");

        if (!smsVerifier.verify(mobile, verifyCode)) return new ResponseMessage(ErrorCode.FORBIDDEN, "fail to verify");

        User user = userService.getByMobile(mobile);
        if (!user.exists()) user = userService.add(mobile, generateToken(mobile));

        return new ResponseMessage(user);
    }

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, SecretKey.get() }, "|"));
    }
}
