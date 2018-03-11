package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IOrderService;
import com.mmall.service.IRedisPoolService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhengb on 2018-02-19.
 */
@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVO>> getOrderList(HttpServletRequest servletRequest,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
//        String loginToken = CookieUtil.readLoginToken(servletRequest);
//        String userJsonStr = iRedisPoolService.get(loginToken);
//        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
//
//        if (currentUser == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
//                    "用户未登录");
//        }
//
//        //非管理员
//        if (!iUserService.checkIsAdmin(currentUser).isSuccess()) {
//            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
//        } else {
//            return iOrderService.manageList(pageNum, pageSize);
//        }

        return iOrderService.manageList(pageNum, pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> getDetail(HttpServletRequest servletRequest,
                                             @RequestParam("orderNo") Long orderNo) {
//        String loginToken = CookieUtil.readLoginToken(servletRequest);
//        String userJsonStr = iRedisPoolService.get(loginToken);
//        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
//
//        if (currentUser == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
//                    "用户未登录");
//        }
//        //非管理员
//        if (!iUserService.checkIsAdmin(currentUser).isSuccess()) {
//            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
//        } else {
//            return iOrderService.manageDetail(orderNo);
//        }

        return iOrderService.manageDetail(orderNo);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVO>> orderSearch(HttpServletRequest servletRequest, @RequestParam("orderNo") Long orderNo,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
//        String loginToken = CookieUtil.readLoginToken(servletRequest);
//        String userJsonStr = iRedisPoolService.get(loginToken);
//        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
//
//        if (currentUser == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
//                    "用户未登录");
//        }
//        //非管理员
//        if (!iUserService.checkIsAdmin(currentUser).isSuccess()) {
//            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
//        } else {
//            return iOrderService.manageSearch(orderNo, pageSize, pageNum);
//        }

        return iOrderService.manageSearch(orderNo, pageSize, pageNum);
    }

}
