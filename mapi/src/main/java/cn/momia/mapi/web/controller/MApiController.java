package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MApiController extends AbstractController {
    @RequestMapping(value = "/m/**", method = { RequestMethod.GET, RequestMethod.POST })
    public String processMRequest(HttpServletRequest request) {
        return forward(request, request.getRequestURI().substring(2));
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST })
    public String processRequest(HttpServletRequest request) {
        return forward(request);
    }

    @RequestMapping(value = "/v{\\d+}/**", method = { RequestMethod.GET, RequestMethod.POST })
    public String notFound() {
        return "forward:/error/404";
    }
}
