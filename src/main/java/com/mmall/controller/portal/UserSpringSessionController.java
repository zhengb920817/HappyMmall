package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by zhengb on 2018-01-17.
 */
@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {

    @Autowired
    private IUserService iUserService;

    /**
    登录操作
     */
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(@RequestParam("username") String userName,
                                      @RequestParam("password") String password,
                                      HttpSession session) {
        ServerResponse<User> response = iUserService.login(userName, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
    退出登录
     */
    @ResponseBody
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    public ServerResponse<String> logOut(HttpSession session) {
        //String loginToken = CookieUtil.readLoginToken(servletRequest);
        session.setAttribute(Const.CURRENT_USER, null);

        return ServerResponse.createBySuccess();
    }
    /**
    获取当前用户信息
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess(user);
    }

}
