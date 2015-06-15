package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
public class AuthController extends AbstractController {
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String send(HttpServletRequest request)  {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
