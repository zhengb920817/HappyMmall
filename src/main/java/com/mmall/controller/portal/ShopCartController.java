package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IRedisPoolService;
import com.mmall.service.IShopCartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.FastJsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhengb on 2018-02-07.
 */
@Controller
@RequestMapping(value = "/cart/")
public class ShopCartController {

    @Autowired
    private IShopCartService iShippingCartService;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse<CartVO> add(HttpServletRequest servletRequest,
                                      @RequestParam("productId") Integer productId,
                                      @RequestParam("count") Integer count) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingCartService.add(currentUser.getId(), productId, count);
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpServletRequest servletRequest,
                                         @RequestParam("productId") Integer productId,
                                         @RequestParam("count") Integer count) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingCartService.update(currentUser.getId(), productId, count);
    }

    @RequestMapping(value = "delete_product.do")
    @ResponseBody
    public ServerResponse<CartVO> deleteProduct(HttpServletRequest servletRequest,
                                         @RequestParam("productIds") String productIds) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.deleteProduct(currentUser.getId(), productIds);

    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<CartVO> getList(HttpServletRequest servletRequest) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.getList(currentUser.getId());
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping(value = "select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(HttpServletRequest servletRequest) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(currentUser.getId(),
                null, Const.CartStaus.CHECKED.getCode());
    }

    @RequestMapping(value = "un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(HttpServletRequest servletRequest) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(currentUser.getId(), null,
                Const.CartStaus.UNCHECKED.getCode());
    }

    @RequestMapping(value = "select.do")
    @ResponseBody
    public ServerResponse<CartVO> selectProduct(HttpServletRequest servletRequest,
                                                    @RequestParam("productId") Integer productId) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(currentUser.getId(), productId,
                Const.CartStaus.CHECKED.getCode());
    }

    @RequestMapping(value = "un_select.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectProduct(HttpServletRequest servletRequest,
                                                @RequestParam("productId") Integer productId) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(currentUser.getId(), productId,
                    Const.CartStaus.UNCHECKED.getCode());
    }

    @RequestMapping(value = "get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest servletRequest) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.getCartProductCount(currentUser.getId());
    }



}
