package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IRedisPoolService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.FastJsonUtil;
import com.mmall.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author zhengb
 * Created by zhengb on 2018-01-30.
 */
@Controller
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpSession httpSession, HttpServletResponse servletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.RegRole.ADMIN.getUserType()) {
                String token = UuidUtil.getUUIDString();
                CookieUtil.writeLoginToken(servletResponse, token);
                //存到redis中
                iRedisPoolService.setex(token, FastJsonUtil.obj2JsonStr(response.getData()),
                        Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }

        return response;
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
       return iUserService.registerAdmin(user);
    }
}
