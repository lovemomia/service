package cn.momia.mapi.api.v1;

import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/payment")
public class PaymentApi extends AbstractApi {
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseMessage checkPayment(HttpServletRequest request) {
        return new ResponseMessage("TODO");
    }
}
