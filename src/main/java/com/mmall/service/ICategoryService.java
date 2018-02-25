package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by zhengb on 2018-02-01.
 */
public interface ICategoryService {

    /**
     * 增加品类
     * @param categoryName 品类名称
     * @param parentId 父品类id
     * @return
     */
    ServerResponse addCategory(String categoryName, Integer parentId);

    /**
     * 根据id更新品类
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    /**
     * 根据id获取其子类品类详情
     * @param categoryId
     * @return
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    /**
     * 根据品类id递归获取其子类
     * @param categoryId
     * @return
     */
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
