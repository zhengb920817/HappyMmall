package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IRedisPoolService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.FastJsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by zhengb on 2018-01-17.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    /**
    登录操作
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpServletResponse servletResponse,
                                      @RequestParam("username") String userName,
                                      @RequestParam("password") String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(userName, password);
        if (response.isSuccess()) {
            CookieUtil.writeLoginToken(servletResponse, session.getId());
            //存到redis中
            iRedisPoolService.setex(session.getId(), FastJsonUtil.obj2JsonStr(response.getData()),
                    Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
    退出登录
     */
    @ResponseBody
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    public ServerResponse<String> logOut(HttpServletRequest servletRequest,
                                         HttpServletResponse servletResponse) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        /**
         * 删除cookie
         */
        CookieUtil.delLoginCookie(servletRequest, servletResponse);
        /**
         * 删除redis中的key
         */
        iRedisPoolService.delKey(loginToken);

        return ServerResponse.createBySuccess();
    }

    /**
    注册用户
     */
    @ResponseBody
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    public ServerResponse<String> registerUser(User user){

        return iUserService.registerUser(user);
    }

    /**
    检查用户信息是否有效 校验用户名和邮箱
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }

    /**
    获取当前用户信息
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest servletRequest) {

        String cookieValue = CookieUtil.readLoginToken(servletRequest);

        String userJsonStr = iRedisPoolService.get(cookieValue);

        if (StringUtils.isBlank(userJsonStr)) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }

        User user = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        return ServerResponse.createBySuccess(user);
    }

    /**
    获取忘记密码问题
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(@RequestParam("username") String username){
        return iUserService.forgeGetQustion(username);
    }

    /**
    校验忘记密码答案
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkForgetAnswser(@RequestParam("username") String username,
                                                     @RequestParam("question") String question,
                                                     @RequestParam("answer") String answer) {
        return iUserService.checkForgetQuestionAnswer(username, question, answer);
    }

    /**
    忘记密码下重置密码
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassowrd(@RequestParam("username") String username,
                                                      @RequestParam("passwordnew") String passwordNew,
                                                         @RequestParam("forgettoken") String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
    登录状态下重置密码
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest servletRequest, @RequestParam("passwordOld") String passwordOld,
                                                @RequestParam("passwordNew") String passwordNew) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }

        return iUserService.resetPassword(passwordOld, passwordNew, currentUser);
    }

    /**
    更新用户信息
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInformation(HttpServletRequest servletRequest, User user) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);

        String userJsonStr = iRedisPoolService.get(loginToken);

        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }

        //id和username不能被更新
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateUserInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            iRedisPoolService.setex(loginToken, FastJsonUtil.obj2JsonStr(response.getData()),
                    Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest servletRequest){

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "未登录，需要强制登录，status=10");
        }

        return iUserService.getUserInformation(currentUser.getId());
    }


}
