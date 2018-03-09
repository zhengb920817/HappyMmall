package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.service.IRedisPoolService;
import com.mmall.util.CookieUtil;
import com.mmall.util.FastJsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by zhengb on 2018-03-06.
 */
public class SessionExpireFilter implements Filter{

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        if (StringUtils.isNotEmpty(loginToken)) {
            String jsonStr = iRedisPoolService.get(loginToken);

            if (jsonStr != null) {
                User user = FastJsonUtil.jsonstr2Object(jsonStr, User.class);
                if (user != null) {
                    //user不为空 则重置session的shijiann
                    iRedisPoolService.expire(loginToken, Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
