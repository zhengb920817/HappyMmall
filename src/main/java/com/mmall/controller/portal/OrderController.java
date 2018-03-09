package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IRedisPoolService;
import com.mmall.util.CookieUtil;
import com.mmall.util.FastJsonUtil;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengb on 2018-02-10.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @RequestMapping(value = "pay.do")
    @ResponseBody
    /**
     * 支付操作
     */
    public ServerResponse<Map<String,String>> pay(HttpServletRequest servletRequest,
                                                   @RequestParam("orderNo") Long orderNo) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        String uploadPath = servletRequest.getServletContext().getRealPath("upload");

        return iOrderService.pay(orderNo, currentUser.getId(), uploadPath);
    }

    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    /**
     * 支付宝回调
     */
    public String alipayCallBack(HttpServletRequest servletRequest) {
        Map<String, String[]> requestMap = servletRequest.getParameterMap();
        Map<String, String> params = new HashMap<>();
        //支付支付宝回到过来的参数 组装成map
        for (Map.Entry<String, String[]> requestParams : requestMap.entrySet()) {
            String valueStr = "";
            String paramKey = requestParams.getKey();
            String[] paramValue = requestParams.getValue();

            for (int i = 0; i < paramValue.length; i++) {
                // valueStr = (i == paramValue.length-1) ? valueStr: valueStr + paramValue[i] + ",";
                valueStr = (i == paramValue.length - 1) ? valueStr + paramValue[i] : valueStr + paramValue[i] + ",";
            }

            params.put(paramKey, valueStr);
        }
        /**
        支付宝异步回调通知参数<a>https://docs.open.alipay.com/59/103666</a>
        */
        logger.info("支付宝回调：sign:{},trade_status:{},参数:{}",
                params.get("sign"), params.get("trade_status"), params.toString());

        //验证回调的正确性
        if (params.containsKey("sign_type")) {
            params.remove("sign_type");
        }


        ServerResponse<String> response = iOrderService.alipayCallBack(params);
        if (response.isSuccess()){
            return Const.AliPayCallBack.RESPONSE_SUCCESS;
        }

        return Const.AliPayCallBack.RESPONSE_FAILED;
    }

    @RequestMapping(value = "query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest servletRequest, Long orderNo) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse<Boolean> response = iOrderService.queryOrderPayStatus(currentUser.getId(), orderNo);
        if(response.isSuccess()){
            return ServerResponse.createBySuccess(Boolean.TRUE);
        }
        return ServerResponse.createBySuccess(Boolean.FALSE);
    }

    @RequestMapping(value = "create.do")
    @ResponseBody
    public ServerResponse<OrderVO> create(HttpServletRequest servletRequest,
                                          @RequestParam("shippingId") Integer shippingId) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.createOrder(currentUser.getId(), shippingId);
    }

    @RequestMapping(value = "cancel.do")
    @ResponseBody
    public ServerResponse<String> cancel(HttpServletRequest servletRequest, @RequestParam("orderNo") Long orderNo) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.cancelOrder(currentUser.getId(), orderNo);

    }

    @RequestMapping(value = "get_order_cart_product.do")
    @ResponseBody
    public ServerResponse<OrderProductVO> getOrderCartProduct(HttpServletRequest servletRequest){

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderCarProduct(currentUser.getId());
    }

    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> getDetail(HttpServletRequest servletRequest,
                                             @RequestParam("orderNo") Long orderNo) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }


        return iOrderService.getOderDetail(currentUser.getId(), orderNo);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVO>> getList(HttpServletRequest servletRequest,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderList(currentUser.getId(), pageNum, pageSize);
    }

    @RequestMapping(value = "send_goods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(HttpServletRequest servletRequest, @RequestParam("orderNo") Long orderNo) {

        String loginToken = CookieUtil.readLoginToken(servletRequest);
        String userJsonStr = iRedisPoolService.get(loginToken);
        User currentUser = FastJsonUtil.jsonstr2Object(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.manageSendGoods(orderNo);
    }
}
