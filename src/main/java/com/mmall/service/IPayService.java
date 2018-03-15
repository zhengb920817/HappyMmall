package com.mmall.service;

import com.mmall.common.pay.AliPayInfo;
import com.mmall.common.pay.PayPrecreateResponse;
import com.mmall.common.pay.PayResult;
import com.mmall.common.pay.RefundInfo;

/**
 * Created by zhengb on 2018-02-11.
 * @author zhengb
 * 支付服务接口定义
 */
public interface IPayService {
    /**
     * 生成二维码支付信息
     * @param aliPayInfo
     * @return
     */
    PayResult<PayPrecreateResponse> trade_precreate(AliPayInfo aliPayInfo);

    /**
     * 交易退款
     * @param refundInfo
     * @return
     */
    boolean trade_Refund(RefundInfo refundInfo);
}
