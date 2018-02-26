package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Created by zhengb on 2018-02-08.
 * 收货地址controller
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    //添加收货地址
    public ServerResponse<Map<String, Integer>> add(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.add(user.getId(),shipping);
    }

    @RequestMapping(value = "del.do")
    @ResponseBody
    //添加收货地址
    public ServerResponse<String> delete(HttpSession session, @RequestParam("shippingId") Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    //更新收货地址
    public ServerResponse<String> update(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping(value = "select.do")
    @ResponseBody
    //更新收货地址
    public ServerResponse<Shipping> select(HttpSession session, @RequestParam("shippingId") Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo<Shipping>> getList(HttpSession session,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.getList(user.getId(), pageNum, pageSize);

    }

}
