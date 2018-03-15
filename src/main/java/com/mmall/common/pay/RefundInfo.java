package com.mmall.common.pay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by zhengb on 2018-03-14.
 * @author zhengb
 * 支付宝退款信息
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RefundInfo {

    private Integer userId;

    /**
     * (必填) 外部订单号，需要退款交易的商户外部订单号
     */
    private String outTradNo;
    /**
     * (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
     */
    private BigDecimal refundAmout;
    /**
     * 必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
     */
    private String refundReason;

    /**
     * (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用
     */
    private String storeId;

    /**
     * (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请
     */
    private String outRequestNo;
}
