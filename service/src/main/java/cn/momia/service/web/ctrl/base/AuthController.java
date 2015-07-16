package cn.momia.service.web.ctrl.base;

import cn.momia.common.misc.ValidateUtil;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.base.user.User;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.sms.SmsSender;
import cn.momia.service.sms.SmsVerifier;
import cn.momia.service.sms.impl.SmsLoginException;
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
public class AuthController extends UserRelatedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired private SmsSender smsSender;
    @Autowired private SmsVerifier smsVerifier;

    @Autowired private CouponService couponService;

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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMessage register(@RequestParam(value = "nickname") String nickName, @RequestParam String mobile, @RequestParam String password, @RequestParam String code){
        if (StringUtils.isBlank(nickName) || ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(password) || StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;
        if (userService.getByNickName(nickName).exists()) return ResponseMessage.FAILED("注册失败，用户昵称已存在");
        if (!smsVerifier.verify(mobile, code)) return ResponseMessage.FAILED("验证码不正确");

        User user = userService.getByMobile(mobile);
        if (user.exists()) return ResponseMessage.FAILED("注册失败，用户已存在");

        String token = generateToken(mobile);
        user = userService.add(nickName, mobile, password, token);
        if (!user.exists()) {
            LOGGER.error("fail to register user for {}", mobile);
            return ResponseMessage.FAILED("注册失败");
        }

        long userCouponId = couponService.getUserRegisterCoupon(user.getId());

        return new ResponseMessage(buildUserResponse(user, userCouponId));
    }

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, new Date().toString(), SecretKey.get() }, "|"));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMessage login(@RequestParam String mobile, @RequestParam String password) {
        LOGGER.info("login by password: {}", mobile);

        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(password)) return ResponseMessage.BAD_REQUEST;
        if (!userService.validatePassword(mobile, password)) return ResponseMessage.FAILED("登录失败，密码不正确");

        User user = userService.getByMobile(mobile);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FORBIDDEN, "登录失败，用户账号被禁用");

        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/login/code", method = RequestMethod.POST)
    public ResponseMessage loginByCode(@RequestParam String mobile, @RequestParam String code) {
        LOGGER.info("login by code: {}", mobile);

        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;
        if (!smsVerifier.verify(mobile, code)) return ResponseMessage.FAILED("验证码不正确");

        User user = userService.getByMobile(mobile);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FORBIDDEN, "登录失败，用户账号被禁用");

        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public ResponseMessage updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(password) || StringUtils.isBlank(code)) return ResponseMessage.BAD_REQUEST;
        if (!smsVerifier.verify(mobile, code)) return ResponseMessage.FAILED("验证码不正确");

        User user = userService.getByMobile(mobile);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FORBIDDEN, "用户不存在");

        if (!userService.updatePassword(user.getId(), mobile, password)) return ResponseMessage.FAILED("更改密码失败");
        return new ResponseMessage(buildUserResponse(user));
    }
}
