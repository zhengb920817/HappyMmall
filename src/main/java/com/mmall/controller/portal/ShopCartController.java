package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IShopCartService;
import com.mmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by zhengb on 2018-02-07.
 */
@Controller
@RequestMapping(value = "/cart/")
public class ShopCartController {

    @Autowired
    private IShopCartService iShippingCartService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse<CartVO> add(HttpSession session,
                                      @RequestParam("productId") Integer productId,
                                      @RequestParam("count") Integer count) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingCartService.add(user.getId(), productId, count);
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpSession session,
                                         @RequestParam("productId") Integer productId,
                                         @RequestParam("count") Integer count) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingCartService.update(user.getId(), productId, count);
    }

    @RequestMapping(value = "delete_product.do")
    @ResponseBody
    public ServerResponse<CartVO> deleteProduct(HttpSession session,
                                         @RequestParam("productIds") String productIds) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.deleteProduct(user.getId(), productIds);

    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<CartVO> getList(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.getList(user.getId());
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping(value = "select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(user.getId(), null, Const.CartStaus.CHECKED.getCode());
    }

    @RequestMapping(value = "un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(user.getId(), null, Const.CartStaus.UNCHECKED.getCode());
    }

    @RequestMapping(value = "select.do")
    @ResponseBody
    public ServerResponse<CartVO> selectProduct(HttpSession session,
                                                    @RequestParam("productId") Integer productId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(user.getId(), productId,
                Const.CartStaus.CHECKED.getCode());
    }

    @RequestMapping(value = "un_select.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectProduct(HttpSession session,
                                                @RequestParam("productId") Integer productId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.selectAllorUnSelect(user.getId(), productId,
                    Const.CartStaus.UNCHECKED.getCode());
    }

    @RequestMapping(value = "get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingCartService.getCartProductCount(user.getId());
    }



}
