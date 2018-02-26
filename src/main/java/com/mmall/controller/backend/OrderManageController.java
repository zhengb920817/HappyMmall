package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVO>> getOrderList(HttpSession session,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        User curLoginUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }

        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            return iOrderService.manageList(pageNum, pageSize);
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> getDetail(HttpSession session, @RequestParam("orderNo") Long orderNo) {
        User curLoginUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }
        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            return iOrderService.manageDetail(orderNo);
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVO>> orderSearch(HttpSession session, @RequestParam("orderNo") Long orderNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        User curLoginUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }
        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            return iOrderService.manageSearch(orderNo, pageSize, pageNum);
        }
    }

}
