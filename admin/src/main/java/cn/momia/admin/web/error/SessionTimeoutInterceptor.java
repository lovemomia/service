package cn.momia.admin.web.error;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.momia.admin.web.entity.AdminUser;

/**
 * Created by hoze on 15/6/16.
 */


/**
 * 处理session超时的拦截器
 */
public class SessionTimeoutInterceptor implements HandlerInterceptor{

    public String[] allowUrls;//还没发现可以直接配置不拦截的资源，所以在代码里面来排除

    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
        //System.out.println(requestUrl);
        if(null != allowUrls && allowUrls.length>=1){
            for(String url : allowUrls) {
                if(requestUrl.contains(url)) {
                    return true;
                }
            }
        }
        AdminUser user = (AdminUser) request.getSession().getAttribute("user");
        if(user != null) {
            return true;  //返回true，则这个方面调用后会接着调用postHandle(),  afterCompletion()
        }else{
            // 未登录  跳转到登录页面
            throw new SessionTimeoutException();//返回到配置文件中定义的路径
        }
    }

    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }



}
