package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/favorite")
public class FavoriteController extends AbstractController {
    @RequestMapping(method = RequestMethod.POST)
    public String addFavorite(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteFavorite(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getFavoritesOfUser(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
