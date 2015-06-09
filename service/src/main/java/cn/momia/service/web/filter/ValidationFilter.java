package cn.momia.service.web.filter;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.secret.SecretKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ValidationFilter implements Filter {
    private Configuration conf;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        conf = (Configuration) webApplicationContext.getBean("conf");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String userAgent = httpRequest.getHeader("user-agent");
        String expired = httpRequest.getParameter("expired");
        // TODO other required params
        String sign = httpRequest.getParameter("sign");
        if (StringUtils.isBlank(userAgent) ||
                StringUtils.isBlank(expired) ||
                StringUtils.isBlank(sign)) {
            forwardErrorPage(request, response, 400);
            return;
        }

        if (System.currentTimeMillis() > Long.valueOf(expired) ||
                !sign.equals(DigestUtils.md5Hex(StringUtils.join(new String[] { expired, /** other params **/SecretKey.get() }, "|")))) {
            forwardErrorPage(request, response, 403);
            return;
        }

        chain.doFilter(request, response);
    }

    private void forwardErrorPage(ServletRequest request, ServletResponse response, int errorCode) throws ServletException, IOException {
        request.getRequestDispatcher("/error/" + errorCode).forward(request, response);
    }

    @Override
    public void destroy() {
    }

    public static void main(String[] args)
    {
        System.out.println(DigestUtils.md5Hex(StringUtils.join(new String[] { "1500000000000", /** other params **/"578890d82212ae548d883bc7a201cdf4" }, "|")));
    }
}
