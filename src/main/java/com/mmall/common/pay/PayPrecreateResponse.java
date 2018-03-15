package com.mmall.common.pay;

import lombok.Data;

/**
 * Created by zhengb on 2018-02-11.
 * 二维码支付返回结果信息
 */
@Data
public class PayPrecreateResponse {
    private String tradeNo;
    private String qrCode;
    private String body;
}
