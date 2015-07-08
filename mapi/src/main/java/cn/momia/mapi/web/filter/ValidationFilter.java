package cn.momia.mapi.web.filter;

import cn.momia.common.web.secret.SecretKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

public class ValidationFilter implements Filter {
    private static final Pattern VERSION_PATTERN = Pattern.compile("^/v\\d+/");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (isUserAgentMissing(httpRequest)) {
            forwardErrorPage(request, response, 400);
            return;
        }

        if (needParamsValidation(httpRequest)) {
            if (isParamMissing(httpRequest))
            {
                forwardErrorPage(request, response, 400);
                return;
            }

            if (isInvalidUri(httpRequest) || isInvalidSign(httpRequest))
            {
                forwardErrorPage(request, response, 403);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isUserAgentMissing(HttpServletRequest httpRequest) {
        String userAgent = httpRequest.getHeader("user-agent");

        return StringUtils.isBlank(userAgent);
    }

    private boolean needParamsValidation(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return !(uri.startsWith("/callback") || uri.startsWith("/m/"));
    }

    private boolean isParamMissing(HttpServletRequest httpRequest) {
        String version = httpRequest.getParameter("v");
        String teminal = httpRequest.getParameter("terminal");
        String os = httpRequest.getParameter("os");
        String device = httpRequest.getParameter("device");
        String channel = httpRequest.getParameter("channel");
        String net = httpRequest.getParameter("net");
        String sign = httpRequest.getParameter("sign");

        return (StringUtils.isBlank(version) ||
                StringUtils.isBlank(teminal) ||
                StringUtils.isBlank(os) ||
                StringUtils.isBlank(device) ||
                StringUtils.isBlank(channel) ||
                StringUtils.isBlank(net) ||
                StringUtils.isBlank(sign));
    }

    private boolean isInvalidUri(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (VERSION_PATTERN.matcher(uri).find()) return true;
        return false;
    }

    private boolean isInvalidSign(HttpServletRequest httpRequest) {
        StringBuilder builder = new StringBuilder();
        builder.append("channel=").append(httpRequest.getParameter("channel")).append("&")
                .append("device=").append(httpRequest.getParameter("device")).append("&")
                .append("expired=").append(httpRequest.getParameter("expired")).append("&")
                .append("net=").append(httpRequest.getParameter("net")).append("&")
                .append("os=").append(httpRequest.getParameter("os")).append("&")
                .append("terminal=").append(httpRequest.getParameter("terminal")).append("&")
                .append("v=").append(httpRequest.getParameter("v")).append("&")
                .append("key=").append(SecretKey.get());

        String sign = httpRequest.getParameter("sign");

        return !sign.equals(DigestUtils.md5Hex(builder.toString()));
    }

    private void forwardErrorPage(ServletRequest request, ServletResponse response, int errorCode) throws ServletException, IOException {
        request.getRequestDispatcher("/error/" + errorCode).forward(request, response);
    }

    @Override
    public void destroy() {}
}
