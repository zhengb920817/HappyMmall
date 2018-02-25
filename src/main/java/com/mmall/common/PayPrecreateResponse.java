package com.mmall.common;

/**
 * Created by zhengb on 2018-02-11.
 * 二维码支付返回结果信息
 */
public class PayPrecreateResponse {
    private String tradeNo;
    private String qrCode;
    private String body;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
