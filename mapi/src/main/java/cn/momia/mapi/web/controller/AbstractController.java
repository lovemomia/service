package cn.momia.mapi.web.controller;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController {
    public String forward(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    public String forward(HttpServletRequest request, String uri) {
        return "forward:/" + getApiVersion(request) + uri;
    }

    private String getApiVersion(HttpServletRequest request) {
        return "v1";
    }
}
