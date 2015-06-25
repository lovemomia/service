package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/order")
public class OrderController extends AbstractController {
    @RequestMapping(method = RequestMethod.POST)
    public String placeOrder(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteOrder(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
