package cn.momia.service.course.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/subject/payment")
public class PaymentController {
    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public MomiaHttpResponse prepay(@RequestParam String utoken,
                                    @RequestParam(value = "oid") long orderId,
                                    @RequestParam(defaultValue = "app") String type) {
        return null;
    }

    @RequestMapping(value = "/prepay/weixin", method = RequestMethod.POST)
    public MomiaHttpResponse prepay(@RequestParam String utoken,
                                    @RequestParam(value = "oid") long orderId,
                                    @RequestParam(defaultValue = "app") final String type,
                                    @RequestParam(required = false) String code) {
        return null;
    }
}
