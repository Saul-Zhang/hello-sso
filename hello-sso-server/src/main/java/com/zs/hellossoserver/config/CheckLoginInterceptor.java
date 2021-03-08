package com.zs.hellossoserver.config;

import com.zs.hellossoserver.bean.Users;
import com.zs.hellossoserver.helper.CheckLoginHelper;
import com.zs.hellossoserver.utils.CookieUtils;
import com.zs.hellossoserver.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * @author ZhangSong
 */
public class CheckLoginInterceptor implements HandlerInterceptor {

    @Autowired
    CheckLoginHelper checkLoginHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 检查调用端是否登录
        String user = CookieUtils.getCookie(request, "user");
        if(StringUtils.isNoneBlank(user)){
            user = URLDecoder.decode(user,"UTF-8");
            Users users = JsonUtils.jsonToPojo(user, Users.class);
            if (users != null && checkLoginHelper.verifySession(users.getId())) {
                return true;
            }
        }

        String link = request.getRequestURL().toString();
        // 看是否是校验临时票据的请求
        String tmpTicket = request.getParameter("tmpTicket");
        if (StringUtils.isNoneBlank(tmpTicket)) {
            return true;
        }

        String loginPageUrl = "http://hellosso.com:8089/login?returnUrl=" + link;
        response.sendRedirect(loginPageUrl);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
