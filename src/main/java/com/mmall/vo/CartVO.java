package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhengb on 2018-02-07.
 */

/**
 * 返回给前端的购物车信息
 */
@Getter
@Setter
public class CartVO {

    /**
     * 购物车中商品列表
     */
    private List<CartProductVO> cartProductVOList;

    /**
     * 当前购物车所有商品总价
     */
    private BigDecimal cartTotalPrice;

    /**
     * 购物车中商品是否全部选中
     */
    private boolean allChecked;

    private String imageHost;
}
