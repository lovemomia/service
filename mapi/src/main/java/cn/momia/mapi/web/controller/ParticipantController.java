package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/participant")
public class ParticipantController extends AbstractController {
    @RequestMapping(method = RequestMethod.POST)
    public String addParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getParticipantsOfUser(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
