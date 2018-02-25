package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCatByUserIdAndProductId(@Param("productId") Integer productId, @Param("userId") Integer userId);

    List<Cart> selectCartListByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteCartByUserIdAndProductId(@Param("userId") Integer userId,
                                       @Param("productIdList") List<String> productIdList);

//    int checkedOrUnCheckedAllProduct(@Param("userId") Integer userId, @Param("checked") Integer checked);

    int checkedOrUnCheckedProduct(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked
    );

    int selectCartProductCount(Integer userId);

    List<Cart> selectCheckedCaryByUserId(Integer userId);
}