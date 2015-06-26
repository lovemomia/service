package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/city")
public class CityController extends AbstractController {
    @RequestMapping(method = RequestMethod.GET)
    public String getAllCities(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
