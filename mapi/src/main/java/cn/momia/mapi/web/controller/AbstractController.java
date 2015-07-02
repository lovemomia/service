package cn.momia.mapi.web.controller;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController {
    public String forward(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    public String forward(HttpServletRequest request, String uri) {
        return "forward:/" + getApiVersion(request) + uri;
    }

    private String getApiVersion(HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        String teminal = request.getParameter("teminal");
//
//        if (uri.equalsIgnoreCase("/home") && !StringUtils.isBlank(teminal)) return "v2";
        return "v1";
    }
}
