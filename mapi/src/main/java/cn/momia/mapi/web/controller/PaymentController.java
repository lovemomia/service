package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/payment")
public class PaymentController extends AbstractController {
    @RequestMapping(value = "/prepay/alipay", method = RequestMethod.POST)
    public String prepayAlipay(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/prepay/wechatpay", method = RequestMethod.POST)
    public String prepayWechatpay(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public String checkPayment(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
