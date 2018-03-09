package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by zhengb on 2018-02-01.
 */
@Service("iCategoryService")
@Slf4j
public class CategoryServiceImp implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional
    public ServerResponse<String> addCategory(String categoryName, Integer parentId){
        if(StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类错误，参数错误");
        }

        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);

        int resultCount = categoryMapper.insert(category);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        else {
            return ServerResponse.createByErrorMessage("添加品类失败");
        }

    }

    @Transactional
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName){
        if(StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category updateCategory = new Category();
        updateCategory.setName(categoryName);
        updateCategory.setId(categoryId);

        //选择性的更新
        int rowCount = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新品类成功");
        }

        return  ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    @Transactional
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);

        if(CollectionUtils.isEmpty(categoryList)){
            log.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
    递归查询本节点和子节点孩子的id
     */
    @Transactional
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newConcurrentHashSet();
        findChildCategory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for (Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }

        return ServerResponse.createBySuccess(categoryIdList);
    }

    /**
     *
     * @param categorySet
     * @param categoryId
     * @return
     */
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);

        for(Category item: categoryList){
            findChildCategory(categorySet, item.getId());
        }

        return categorySet;
    }



}
