package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewUser(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/view/order", method = RequestMethod.GET)
    public String viewOrders(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getUser(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public String updateAvatar(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/name", method = RequestMethod.POST)
    public String updateName(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/sex", method = RequestMethod.POST)
    public String updateSex(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.POST)
    public String updateBirthday(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/city", method = RequestMethod.POST)
    public String updateCity(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public String updateAddress(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
