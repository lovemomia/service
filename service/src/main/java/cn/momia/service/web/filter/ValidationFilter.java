package cn.momia.service.web.filter;

import cn.momia.common.web.http.MomiaHttpRequestUtils;
import cn.momia.common.web.secret.SecretKey;
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
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (isParamMissing(httpRequest))
        {
            forwardErrorPage(request, response, 400);
            return;
        }

        if (isInvalidProtocol(httpRequest) || isExpired(httpRequest) || isInvalidSign(httpRequest))
        {
            forwardErrorPage(request, response, 403);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isParamMissing(HttpServletRequest httpRequest) {
        String expired = httpRequest.getParameter("expired");
        String sign = httpRequest.getParameter("sign");

        return StringUtils.isBlank(expired) || StringUtils.isBlank(sign);
    }

    private boolean isInvalidProtocol(HttpServletRequest request) {
        String schema = request.getScheme();
        String uri = request.getRequestURI();

        if (!(uri.startsWith("/callback") || uri.startsWith("/order") || uri.startsWith("/payment"))) return false;
        if (!schema.equals("https")) return true;
        return false;
    }

    private boolean isExpired(HttpServletRequest httpRequest) {
        String expired = httpRequest.getParameter("expired");

        return System.currentTimeMillis() > Long.valueOf(expired);
    }

    private boolean isInvalidSign(HttpServletRequest httpRequest) {
        String sign = httpRequest.getParameter("sign");

        return !sign.equals(MomiaHttpRequestUtils.sign(MomiaHttpRequestUtils.extractParams(httpRequest), SecretKey.get()));
    }

    private void forwardErrorPage(ServletRequest request, ServletResponse response, int errorCode) throws ServletException, IOException {
        request.getRequestDispatcher("/error/" + errorCode).forward(request, response);
    }

    @Override
    public void destroy() {
    }
}
