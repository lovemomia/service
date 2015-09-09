package cn.momia.service.base.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.base.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController {
    @Autowired private SmsService smsService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public MomiaHttpResponse send(@RequestParam String mobile, @RequestParam String type) {
        if (!smsService.sendCode(mobile, type)) return MomiaHttpResponse.FAILED("发送短信验证码失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public MomiaHttpResponse verify(@RequestParam String mobile, @RequestParam String code) {
        if (!smsService.verifyCode(mobile, code)) return MomiaHttpResponse.FAILED("验证码不正确");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public MomiaHttpResponse notify(@RequestParam String mobile, @RequestParam String msg) {
        if (!smsService.notifyUser(mobile, msg)) return MomiaHttpResponse.FAILED("发送通知失败");
        return MomiaHttpResponse.SUCCESS;
    }
}
