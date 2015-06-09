package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/callback")
public class CallbackController extends AbstractController {
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public String alipayCallback(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/wechatpay", method = RequestMethod.POST)
    public String wechatpayCallback(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/unionpay", method = RequestMethod.POST)
    public String unionpayCallback(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
