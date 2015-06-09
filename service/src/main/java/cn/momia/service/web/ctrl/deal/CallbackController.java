package cn.momia.service.web.ctrl.deal;

import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/callback")
public class CallbackController {
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public ResponseMessage alipayCallback() {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public ResponseMessage wechatpayCallback() {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/unionpay", method = RequestMethod.POST)
    public ResponseMessage unionpayCallback() {
        return new ResponseMessage("TODO");
    }
}
