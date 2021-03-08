package com.zs.hellossoserver.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhangSong
 */
public class CookieUtils {
    public static void setCookie(String key,
                                 String val,
                                 HttpServletResponse response) {

        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("hellosso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void deleteCookie(String key,
                                    HttpServletResponse response) {

        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("hellosso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String key) {

        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || StringUtils.isBlank(key)) {
            return null;
        }

        String cookieValue = null;
        for (int i = 0; i < cookieList.length; i++) {
            if (cookieList[i].getName().equals(key)) {
                cookieValue = cookieList[i].getValue();
                break;
            }
        }

        return cookieValue;
    }
}
