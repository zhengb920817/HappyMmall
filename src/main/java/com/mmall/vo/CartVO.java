package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhengb on 2018-02-07.
 */

/**
 * 返回给前端的购物车信息
 */
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

    public List<CartProductVO> getCartProductVOList() {
        return cartProductVOList;
    }

    public void setCartProductVOList(List<CartProductVO> cartProductVOList) {
        this.cartProductVOList = cartProductVOList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
