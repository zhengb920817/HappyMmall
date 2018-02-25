package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Created by zhengb on 2018-01-17.
 */
@Service("iUserService")
public class UserServiceImp implements IUserService{

    @Autowired
    private UserMapper userMapper;

    /*
    从TokenCache中根据用户名获取token值
     */
    private String getTokenFromTokenCache(String userName) {
        return TokenCache.getKey(TokenCache.TOKEN_PREFIX + userName);
    }

    /*
    写入用户token值到TokenCache中
     */
    private void setTokenValueToTokenCache(String username, String value) {
        TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, value);
    }

    /*
    获取加密后的密码
     */
    private String getDecrptedPassword(final String password){
        return MD5Util.MD5EncodeUtf8(password);
    }

    @Override
    @Transactional
    public ServerResponse<User> login(String userName, String password) {
        int resultCount = userMapper.checkUserName(userName);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");

        }

        //todo 密码登录md5
        //String md5Password = MD5Util.MD5EncodeUtf8(password);
        String md5Password = getDecrptedPassword(password);
        User selectUser = userMapper.selectLogin(userName, md5Password);
        if(selectUser == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        selectUser.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功",selectUser);
    }

    @Transactional
    public ServerResponse<String> registerUser(User user){
        //校验用户名
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        //校验邮箱
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        User registerUser = user;
        registerUser.setRole(Const.RegRole.CUSTOMER.getUserType());
        registerUser.setPassword(getDecrptedPassword(user.getPassword()));

        int resultCount;

        resultCount = userMapper.insert(registerUser);

        if(resultCount > 0) {
            return ServerResponse.createBySuccessMessage("注册成功");
        }
        else {
            return ServerResponse.createByErrorMessage("注册失败");
        }

    }

    @Transactional
    public ServerResponse<String> checkValid(String str,String  type){
        int resultCount = 0;
        if(StringUtils.isNotBlank(str)){
            if(Const.USERNAME.equals(type)){
                resultCount = userMapper.checkUserName(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }

            if(Const.EMAIL.equals(type)){
                resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccessMessage("校验通过");
    }

    @Transactional
    public ServerResponse<String> forgeGetQustion(@RequestParam("username") String username){
       ServerResponse response = this.checkValid(username, Const.USERNAME);
       if(response.isSuccess()){
           //用户不存在
           return ServerResponse.createByErrorMessage("用户不存在");
       }

       String question = userMapper.selectQuestionByUserName(username);

       if(StringUtils.isNotBlank(question)){
           return ServerResponse.createBySuccessMessage(question);
       }

       return ServerResponse.createByErrorMessage("找回密码问题为空");

    }

    @Transactional
    public ServerResponse<String> checkForgetQuestionAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            //答案正确
            String token = UUID.randomUUID().toString();
            //TokenCache.setKey("token_" + username,token);
            setTokenValueToTokenCache(username, token);
            return ServerResponse.createBySuccess(token);
        }

        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Transactional
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew,
                                                        String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return  ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }

        ServerResponse<String> response = this.checkValid(username,Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = getTokenFromTokenCache(username);

        if(StringUtils.equals(token,forgetToken)){
            String newPassword = getDecrptedPassword(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, newPassword);
            if(resultCount > 0){
                return ServerResponse.createBySuccessMessage("重置密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("Token错误，请重新获取重置密码的Token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Transactional
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        int resultCount = userMapper.checkPassword(user.getId(),getDecrptedPassword(passwordOld));
        if(resultCount <= 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(getDecrptedPassword(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Transactional
    public ServerResponse<User> updateUserInformation(User user){

        //username不能被更新
        //email也要进行校验，校验新的email是否已经存在，已经存在的话
        int resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在，请更换email后再尝试更新");
        }
        User updateUser = new User();
        /*
        只更新这个五个字段
         */
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("更新个人信息成功");
        }

        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Transactional
    public ServerResponse<User> getUserInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //查找到用户 把密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Transactional
    public ServerResponse<String> registerAdmin(User user){
        ServerResponse<String> response = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户已存在");
        }

        User registerAdmin = new User();
        registerAdmin.setUsername(user.getUsername());
        registerAdmin.setPassword(getDecrptedPassword(user.getPassword()));
        registerAdmin.setEmail(user.getEmail());
        registerAdmin.setPhone(user.getPhone());
        registerAdmin.setRole(Const.RegRole.ADMIN.getUserType());

        int resultCount = userMapper.insert(registerAdmin);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("注册成功");
        }

        return ServerResponse.createByErrorMessage("注册失败");

    }

    /*
    校验是否管理员
     */
    @Transactional
    public ServerResponse checkIsAdmin(User user){
        if ( user != null && user.getRole().equals(Const.RegRole.ADMIN.getUserType())) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
