package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IRedisPoolService;
import com.mmall.service.IShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.FastJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by zhengb on 2018-02-08.
 * 收货地址controller
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    //添加收货地址
    public ServerResponse<Map<String, Integer>> add(HttpServletRequest servletRequest, Shipping shipping){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.add(currentUser.getId(),shipping);
    }

    @RequestMapping(value = "del.do")
    @ResponseBody
    //添加收货地址
    public ServerResponse<String> delete(HttpServletRequest servletRequest,
                                         @RequestParam("shippingId") Integer shippingId) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.delete(currentUser.getId(), shippingId);
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    //更新收货地址
    public ServerResponse<String> update(HttpServletRequest servletRequest, Shipping shipping) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.update(currentUser.getId(),shipping);
    }

    @RequestMapping(value = "select.do")
    @ResponseBody
    //更新收货地址
    public ServerResponse<Shipping> select(HttpServletRequest servletRequest,
                                           @RequestParam("shippingId") Integer shippingId) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.select(currentUser.getId(),shippingId);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo<Shipping>> getList(HttpServletRequest servletRequest,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.getList(currentUser.getId(), pageNum, pageSize);

    }

}
