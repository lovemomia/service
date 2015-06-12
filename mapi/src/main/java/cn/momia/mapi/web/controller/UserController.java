package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getUser(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.PUT)
    public String updateAvatar(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/name", method = RequestMethod.PUT)
    public String updateName(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/sex", method = RequestMethod.PUT)
    public String updateSex(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/birthday", method = RequestMethod.PUT)
    public String updateBirthday(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/city", method = RequestMethod.PUT)
    public String updateCity(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.PUT)
    public String updateAddress(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/children", method = RequestMethod.PUT)
    public String updateChildren(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.GET)
    public String getParticipants(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/participant", method = RequestMethod.POST)
    public String addParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.GET)
    public String getParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.PUT)
    public String updateParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/participant/{pid}", method = RequestMethod.DELETE)
    public String deleteParticipant(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/favorite", method = RequestMethod.GET)
    public String getFavorites(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/favorite/{fid}", method = RequestMethod.DELETE)
    public String deleteFavorite(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/order", method = RequestMethod.GET)
    public String getOrders(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
