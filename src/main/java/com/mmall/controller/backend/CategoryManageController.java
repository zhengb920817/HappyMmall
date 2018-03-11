package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IRedisPoolService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @Autowired
    private IRedisPoolService iRedisPoolService;

    private boolean checkUserIsAdmin(User user){
       return (iUserService.checkIsAdmin(user).isSuccess());
    }

    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpServletRequest servletRequest,
                                              String categoryName,
                                              @RequestParam(value = "parentId", defaultValue = "0") int parentId) {

        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpServletRequest servletRequest,Integer categoryId, String categoryName){

        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    /**
       查询子节点的category信息，并且不递归，保持平级
     */
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpServletRequest servletRequest,
                                    @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
       查询子节点的category信息，并且递归
     */
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getChildrenAndDeepChildrenCategory(HttpServletRequest servletRequest,
                                @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){

        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }


}
