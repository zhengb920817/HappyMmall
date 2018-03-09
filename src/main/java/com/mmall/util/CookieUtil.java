package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhengb on 2018-03-06.
 */
@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMAIN = ".happymmall.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    /**
     * 写入cookie
     * @param httpServletResponse
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse httpServletResponse, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        //代表设置在根目录
        cookie.setPath("/");
        //设置为true时，无法通过脚本获取到cookie
        cookie.setHttpOnly(true);
        //单位是秒
        //如果设置为-1，则是永久有效
        //如果是不设置的话cookie就不会写入硬盘，而是写在内存，只在当前页面有效
        //设置一年有效期
        cookie.setMaxAge(60 * 60 * 24 * 365);
        httpServletResponse.addCookie(cookie);
        log.info("writeLoginToken,cookieName:{},cookieValue{}", cookie.getName(), cookie.getValue());
    }

    /**
     * 读取登录cookie
     * @param httpServletRequest
     * @return
     */
    public static String readLoginToken(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {
            log.info("read cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
            if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                log.info("return cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 删除登录cookie
     * @param request
     * @param response
     */
    public static void delLoginCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                cookie.setDomain(COOKIE_DOMAIN);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                log.info("delLoginCookie,cookeName:{},cookieValue{}", cookie.getName(), cookie.getValue());
                /**
                 * 删除一个cookie，即将需要删除的cookie的有效期设置为0
                 */
                response.addCookie(cookie);
                return;
            }
        }
    }

}
