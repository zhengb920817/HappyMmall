package com.mmall.common;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhengb
 * 全局异常处理类
 */
@Component
@Slf4j
public class ExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {

        log.error("{}Exception", request.getRequestURI(), ex);
        ModelAndView modelAndView = new ModelAndView(new FastJsonJsonView());
        modelAndView.addObject("status", ResponseCode.INNER_EXCEPTION.getCode());
        modelAndView.addObject("msg", "接口异常，详情请查看服务端日志");
        modelAndView.addObject("data", ex.toString());
        return modelAndView;
    }
}
