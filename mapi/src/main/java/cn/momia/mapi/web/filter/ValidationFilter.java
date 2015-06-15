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
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

    /*    if (isParamMissing(httpRequest))
        {
            forwardErrorPage(request, response, 400);
            return;
        }

<<<<<<< HEAD:mapi/src/main/java/cn/momia/mapi/web/filter/ValidationFilter.java
        if (isInvalidProtocol(httpRequest) || isInvalidSign(httpRequest))
=======
        */
        if (isInvalidProtocol(httpRequest) )
        {
            forwardErrorPage(request, response, 403);
            return;
        }
/*
        if (isInvalidProtocol(httpRequest) || isExpired(httpRequest) || isInvalidSign(httpRequest))
>>>>>>> ProductController is completed:service/src/main/java/cn/momia/service/web/filter/ValidationFilter.java
        {
            forwardErrorPage(request, response, 403);
            return;
        }
*/
        chain.doFilter(request, response);
    }

    private boolean isParamMissing(HttpServletRequest httpRequest) {
        String userAgent = httpRequest.getHeader("user-agent");
        String expired = httpRequest.getParameter("expired");
        String sign = httpRequest.getParameter("sign");

        return StringUtils.isBlank(userAgent) || StringUtils.isBlank(expired) || StringUtils.isBlank(sign);
    }

    private boolean isInvalidProtocol(HttpServletRequest request) {
        String schema = request.getScheme();
        String uri = request.getRequestURI();

        if (!(uri.startsWith("/callback") || uri.startsWith("/order") || uri.startsWith("/payment"))) return false;
        if (!schema.equals("https")) return true;
        return false;
    }

    private boolean isInvalidSign(HttpServletRequest httpRequest) {
        String expired = httpRequest.getParameter("expired");
        String sign = httpRequest.getParameter("sign");

        return System.currentTimeMillis() > Long.valueOf(expired) ||
                !sign.equals(DigestUtils.md5Hex(StringUtils.join(new String[] { expired, SecretKey.get() }, "|")));
    }

    private void forwardErrorPage(ServletRequest request, ServletResponse response, int errorCode) throws ServletException, IOException {
        request.getRequestDispatcher("/error/" + errorCode).forward(request, response);
    }

    @Override
    public void destroy() {
    }
}
