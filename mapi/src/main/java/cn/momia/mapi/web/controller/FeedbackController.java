package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/feedback")
public class FeedbackController extends AbstractController {
    @RequestMapping(method = RequestMethod.POST)
    public String addFeedback(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
