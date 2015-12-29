package cn.momia.service.user.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.sms.SmsService;
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
    public MomiaHttpResponse send(@RequestParam String mobile) {
        return MomiaHttpResponse.SUCCESS(smsService.sendCode(mobile));
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public MomiaHttpResponse verify(@RequestParam String mobile, @RequestParam String code) {
        return MomiaHttpResponse.SUCCESS(smsService.verifyCode(mobile, code));
    }
}
