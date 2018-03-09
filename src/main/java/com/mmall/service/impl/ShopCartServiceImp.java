package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.IShopCartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtils;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengb on 2018-02-07.
 */

/**
 * 购物车服务
 */
@Service("IShopCartService")
@Slf4j
public class ShopCartServiceImp implements IShopCartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count) {
        //为空 返回参数错误
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCatByUserIdAndProductId(productId, userId);
        if (cart == null) {
            //这个产品不在购物车里  需要添加
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.CartStaus.CHECKED.getCode());
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            int resultCount = cartMapper.insert(cartItem);
            if (resultCount > 0) {

            }
        } else {
            //产品已存在  相加数量
            int totalCount = count + cart.getQuantity();
            cart.setQuantity(totalCount);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        return this.getList(userId);
    }

    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO = new CartVO();
        //根据userId先从购物车表中取出信息
        List<Cart> cartList = cartMapper.selectCartListByUserId(userId);
        List<CartProductVO> cartProductVOList = new ArrayList<>();

        //一定要用字符串构造器 否则会丢失精度
        //购物车中所有商品的总价
        BigDecimal totalPrice = new BigDecimal("0");

        for(Cart cartItem:cartList){
            CartProductVO cartProductVO = new CartProductVO();

            Integer productId = cartItem.getProductId();

            cartProductVO.setProductId(productId);
            cartProductVO.setUserId(userId);
            cartProductVO.setId(cartItem.getId());

            //从商品表中取出对应id的商品信息
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product != null){
                //商品名称
                cartProductVO.setProductName(product.getName());
                //商品价格
                cartProductVO.setProductPrice(product.getPrice());
                //商品主图
                cartProductVO.setProductMainImage(product.getMainImage());
                //商品库存
                cartProductVO.setProductStock(product.getStock());
                //商品副标题
                cartProductVO.setProductSubtitle(product.getSubtitle());
                //商品状态（上架或已下架）
                cartProductVO.setProductStatus(product.getStatus());
                //
                int buyLimitCount = 0;
                //库存大于数量
                if(product.getStock() >= cartItem.getQuantity()){
                    buyLimitCount = cartItem.getQuantity();
                    cartProductVO.setLimitQuantity(Const.LIMIT_NUM_SUCCESS);
                }else{
                    //购买数量是库存
                    buyLimitCount = product.getStock();
                    cartProductVO.setLimitQuantity(Const.LIMIT_NUM_FAIL);
                    //购物车中更新有效库存
                    Cart cartForQuantity = new Cart();
                    cartForQuantity.setProductId(cartItem.getProductId());
                    cartForQuantity.setQuantity(buyLimitCount);
                    cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                }
                //购买数量
                cartProductVO.setQuantity(buyLimitCount);
                //单个商品的总价
                cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),
                        buyLimitCount));
                //勾选状态
                cartProductVO.setProductChecked(cartItem.getChecked());
            }

            //商品是勾选状态 增加到整个购物车总价中
            if (cartItem.getChecked().equals(Const.CartStaus.CHECKED.getCode())){
                totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),
                        cartProductVO.getProductTotalPrice().doubleValue());
            }
            cartProductVOList.add(cartProductVO);
        }
        //总价格
        cartVO.setCartTotalPrice(totalPrice);
        //商品列表
        cartVO.setCartProductVOList(cartProductVOList);
        //是否全选
        cartVO.setAllChecked(getAllCheckedStatus(userId));
        //主图url前缀
        cartVO.setImageHost(PropertiesUtils.getPropertyValue("ftp.server.http.prefix"));
        return cartVO;
    }

    /*
      根据uerId从数据表中获取是否是全选状态(未勾选数量如果0的话那就是全勾选状态)
     */
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null)
            return false;

        int resultCount = cartMapper.selectCartProductCheckedStatusByUserId(userId);
        return resultCount == 0;
    }

    @Transactional
    public ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count){
        //为空 返回参数错误
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCatByUserIdAndProductId(productId, userId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);

        return this.getList(userId);
    }

    @Transactional
    public ServerResponse<CartVO> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteCartByUserIdAndProductId(userId, productList);
        return this.getList(userId);
    }

    @Transactional
    public ServerResponse<CartVO> getList(Integer userId){
        if(userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        CartVO cartVO = getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    //全选或非全选
    @Transactional
    public ServerResponse<CartVO> selectAllorUnSelect(Integer userId, Integer productId, Integer checked) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        cartMapper.checkedOrUnCheckedProduct(userId, productId, checked);

        return this.getList(userId);
    }

    @Transactional
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        int totalCount = cartMapper.selectCartProductCount(userId);

        return ServerResponse.createBySuccess(totalCount);
    }

}
