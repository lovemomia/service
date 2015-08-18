package cn.momia.service.common.web.ctrl;

import cn.momia.service.common.facade.CommonServiceFacade;
import cn.momia.service.base.web.ctrl.AbstractController;
import cn.momia.service.base.web.response.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController extends AbstractController {
    @Autowired private CommonServiceFacade commonServiceFacade;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage send(@RequestParam String mobile, @RequestParam String type) {
        if (!commonServiceFacade.sendCode(mobile, type)) return ResponseMessage.FAILED("发送短信验证码失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ResponseMessage verify(@RequestParam String mobile, @RequestParam String code) {
        if (!commonServiceFacade.verifyCode(mobile, code)) return ResponseMessage.FAILED("验证码不正确");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseMessage notify(@RequestParam String mobile, @RequestParam String msg) {
        if (!commonServiceFacade.notifyUser(mobile, msg)) return ResponseMessage.FAILED("发送通知失败");
        return ResponseMessage.SUCCESS;
    }
}
