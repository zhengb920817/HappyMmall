package com.mmall.common.pay;

import com.mmall.pojo.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhengb on 2018-02-11.
 * 支付宝支付信息
 */
@Data
public class AliPayInfo {
    /**
    交易号
     */
    private String tradeNo;
    /**
    订单标题
     */
    private String subject;
    /**
    订单总金额
     */
    private BigDecimal totalAmout;
    /**
    订单不打折金额
     */
    private BigDecimal undiscountAmout;
    /**
    买家支付宝id
     */
    private String sellerId;
    /**
    订单描述
     */
    private String body;
    /**
    商户操作员编号
     */
    private String opratortId;
    /**
    商户门店编号
     */
    private String storeId;
    /**
    订单商品列表
     */
    private List<OrderItem> orderItemList;

    /**
    支付超时时间
     */
    private String timoeOutExpress;

    /**
    支付宝回调
     */
    private String callbackUrl;
}
