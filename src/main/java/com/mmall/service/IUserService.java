package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by zhengb on 2018-01-17.
 */
public interface IUserService {
    ServerResponse<User> login(String userName,String password);

    ServerResponse<String> registerUser(User user);

    ServerResponse<String> checkValid(String str,String  type);

    ServerResponse<String> forgeGetQustion(String username);

    ServerResponse<String> checkForgetQuestionAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User> updateUserInformation(User user);

    ServerResponse<User> getUserInformation(Integer userId);

    ServerResponse<String> registerAdmin(User user);

    ServerResponse checkIsAdmin(User user);
}
