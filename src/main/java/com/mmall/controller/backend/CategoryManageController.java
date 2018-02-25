package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by zhengb on 2018-02-01.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    private boolean checkUserIsAdmin(User user){
       return (iUserService.checkIsAdmin(user).isSuccess());
    }

    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User curLoginUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }

        if (!checkUserIsAdmin(curLoginUser)) {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无权限操作");
        } else {
            return iCategoryService.addCategory(categoryName, parentId);
        }
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId, String categoryName){
        User curLoginUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(curLoginUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }

        if (!checkUserIsAdmin(curLoginUser)) {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无权限操作");
        }else {
            //更新categoryname
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }
    }

    /*
       查询子节点的category信息，并且不递归，保持平级
     */
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,
                                    @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

        User curLoginUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(curLoginUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }

        if (!checkUserIsAdmin(curLoginUser)) {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无权限操作");
        }else {

            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
    }

    /*
       查询子节点的category信息，并且递归
     */
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getChildrenAndDeepChildrenCategory(HttpSession session,
                                @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){

        User curLoginUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(curLoginUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }

        if (!checkUserIsAdmin(curLoginUser)) {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无权限操作");
        }else {
            //查询当前节点的id和递归子节点的id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
            //return iCategoryService.getChildrenParallelCategory(categoryId);
        }
    }


}
