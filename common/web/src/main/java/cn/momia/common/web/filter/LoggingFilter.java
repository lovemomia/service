package cn.momia.common.web.filter;

import cn.momia.common.web.misc.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LoggingFilter implements Filter
{
    private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("REQUEST");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long end = System.currentTimeMillis();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        REQUEST_LOGGER.info("{}\t{}\t{}ms\t{}\t{}\t{}\t{}", new Object[] { httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                end - start,
                httpRequest.getContentType(),
                httpRequest.getHeader("user-agent"),
                httpRequest.getParameterMap(),
                RequestUtil.getRemoteIp(httpRequest)
        });
    }

    @Override
    public void destroy() {}
}
