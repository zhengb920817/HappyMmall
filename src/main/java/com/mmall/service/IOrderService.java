package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;

import java.util.Map;

/**
 * Created by zhengb on 2018-02-10.
 */
public interface IOrderService {
    /**
     * 支付宝支付 生成支付二维码
     * @param orderNo 订单号
     * @param userId 用户id
     * @param savePath 二维码图片保存路径
     * @return
     */
    ServerResponse pay(Long orderNo, Integer userId, String savePath);

    /**
     * 支付宝回调--校验回调的正确性
     * @param params
     * @return
     */
    ServerResponse alipayCallBack(Map<String,String> params);

    /**
     * 查询支付状态
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    /**
     * 生成订单
     * @param userId 当前用户id
     * @param shippingId 购物车id
     * @return
     */
    ServerResponse<OrderVO> createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<String> cancelOrder(Integer userId, Long orderNo);

    /**
     * 获取当前用户所有勾选订单列表
     * @param userId
     * @return
     */
    ServerResponse<OrderProductVO> getOrderCarProduct(Integer userId);

    /**
     * 根据用户和订单号获取对应订单的详情
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<OrderVO> getOderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize);

    //后台管理员接口
    ServerResponse<PageInfo<OrderVO>> manageList(int pageNum,int pageSize);

    ServerResponse<OrderVO> manageDetail(Long orderNo);

    ServerResponse<PageInfo<OrderVO>> manageSearch(Long orderNo,int pageSize,int pageNum);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
