package cn.momia.mapi.web.controller;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController {
    public String forward(HttpServletRequest request, String uri) {
        return "forward:/" + getApiVersion(request) + uri;
    }

    private String getApiVersion(HttpServletRequest request) {
        // TODO API 可能会需要支持不同版本
        return "v1";
    }
}
