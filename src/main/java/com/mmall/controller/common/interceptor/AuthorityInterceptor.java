package com.mmall.controller.common.interceptor;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IRedisPoolService;
import com.mmall.util.CookieUtil;
import com.mmall.util.FastJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengb on 2018-03-09.
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor{

    private static Object objectMonitor = new Object();

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        log.info("Perhandle " + request.getRequestURI());
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //解析handlerMethod
        //调用方法名manage/user/login.do?username=admin&password=123456
        //调用com.mmall.controller.backend.UserManageController下的login方法
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getName();

        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = Arrays.toString(entry.getValue());

            synchronized (objectMonitor) {
                StringBuilder requestParam = new StringBuilder().append(mapKey).append("=").append(
                        mapValue);
                log.info("登录用户信息{}", requestParam.toString());
            }

        }

        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = iRedisPoolService.get(loginToken);
            user = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        }

        if (user == null || (user.getRole().intValue() != Const.RegRole.ADMIN.getUserType())) {
            //返回false,即不会调用Controller里的方法
            response.reset();
            //这里需要设置编码，否则会产生乱码
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            if(user == null){
                String productManageControllerClassName = "com.mmall.controller.backend.ProductManageController";
                //富文本上传
                if (StringUtils.equals(className, productManageControllerClassName)
                        && StringUtils.equals(methodName, "richTextImageUpload")) {
                    Map<String, Object> retMap = new HashMap<>(2);
                    retMap.put("success", false);
                    retMap.put("msg", "无权限操作");
                    out.print(FastJsonUtil.obj2JsonStr(retMap));
                }
                else {
                    out.print(FastJsonUtil.obj2JsonStr(ServerResponse.createByErrorMessage("用户未登录")));
                }
            }else{
                out.print(FastJsonUtil.obj2JsonStr(ServerResponse.createByErrorMessage("用户无权限操作")));
            }

            out.flush();
            out.close();
            return false;
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        log.info("postHandle " + request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        log.info("afterCompletion " + request.getRequestURI());
    }
}
