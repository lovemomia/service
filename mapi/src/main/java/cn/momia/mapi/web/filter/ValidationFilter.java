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

public class ValidationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

      /*  if (isInvalidProtocol(httpRequest)) {
            forwardErrorPage(request, response, 403);
            return;
        }

        if (!isMWeb(httpRequest)) {
            if (isParamMissing(httpRequest))
            {
                forwardErrorPage(request, response, 400);
                return;
            }

            if (isInvalidUri(httpRequest) || isExpired(httpRequest) || isInvalidSign(httpRequest))
            {
                forwardErrorPage(request, response, 403);
                return;
            }
        }
        */

        chain.doFilter(request, response);
    }

    private boolean isInvalidProtocol(HttpServletRequest request) {
        String schema = request.getScheme();
        String uri = request.getRequestURI();

        if (!(uri.startsWith("/callback") ||
                uri.startsWith("/order") ||
                uri.startsWith("/m/order") ||
                uri.startsWith("/payment") ||
                uri.startsWith("/m/payment"))) return false;
        if (!schema.equals("https")) return true;
        return false;
    }

    private boolean isMWeb(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/m/");
    }

    private boolean isParamMissing(HttpServletRequest httpRequest) {
        String userAgent = httpRequest.getHeader("user-agent");
        String version = httpRequest.getParameter("v");
        String teminal = httpRequest.getParameter("teminal");
        String os = httpRequest.getParameter("os");
        String device = httpRequest.getParameter("device");
        String channel = httpRequest.getParameter("channel");
        String net = httpRequest.getParameter("net");
        String expired = httpRequest.getParameter("expired");
        String sign = httpRequest.getParameter("sign");

        return (StringUtils.isBlank(userAgent) ||
                StringUtils.isBlank(version) ||
                StringUtils.isBlank(teminal) ||
                StringUtils.isBlank(os) ||
                StringUtils.isBlank(device) ||
                StringUtils.isBlank(channel) ||
                StringUtils.isBlank(net) ||
                StringUtils.isBlank(expired) ||
                StringUtils.isBlank(sign));
    }

    private boolean isInvalidUri(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/v")) return true;
        return false;
    }

    private boolean isExpired(HttpServletRequest httpRequest) {
        String expired = httpRequest.getParameter("expired");

        return System.currentTimeMillis() > Long.valueOf(expired);
    }

    private boolean isInvalidSign(HttpServletRequest httpRequest) {
        StringBuilder builder = new StringBuilder();
        builder.append("channel=").append(httpRequest.getParameter("channel")).append("&")
                .append("device=").append(httpRequest.getParameter("device")).append("&")
                .append("expired=").append(httpRequest.getParameter("expired")).append("&")
                .append("net=").append(httpRequest.getParameter("net")).append("&")
                .append("os=").append(httpRequest.getParameter("os")).append("&")
                .append("teminal=").append(httpRequest.getParameter("teminal")).append("&")
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
