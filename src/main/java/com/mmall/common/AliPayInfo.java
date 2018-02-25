package com.mmall.common;

import com.mmall.pojo.OrderItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhengb on 2018-02-11.
 * 支付信息
 */
public class AliPayInfo {
    /*
    交易号
     */
    private String tradeNo;
    /*
    订单标题
     */
    private String subject;
    /*
    订单总金额
     */
    private BigDecimal totalAmout;
    /*
    订单不打折金额
     */
    private BigDecimal undiscountAmout;
    /*
    买家支付宝id
     */
    private String sellerId;
    /*
    订单描述
     */
    private String body;
    /*
    商户操作员编号
     */
    private String opratortId;
    /*
    商户门店编号
     */
    private String storeId;
    /*
    订单商品列表
     */
    private List<OrderItem> orderItemList;

    /*
    支付超时时间
     */
    private String timoeOutExpress;

    /*
    支付宝回调
     */
    private String callbackUrl;


    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public BigDecimal getTotalAmout() {
        return totalAmout;
    }

    public void setTotalAmout(BigDecimal totalAmout) {
        this.totalAmout = totalAmout;
    }

    public BigDecimal getUndiscountAmout() {
        return undiscountAmout;
    }

    public void setUndiscountAmout(BigDecimal undiscountAmout) {
        this.undiscountAmout = undiscountAmout;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOpratortId() {
        return opratortId;
    }

    public void setOpratortId(String opratortId) {
        this.opratortId = opratortId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public String getTimoeOutExpress() {
        return timoeOutExpress;
    }

    public void setTimoeOutExpress(String timoeOutExpress) {
        this.timoeOutExpress = timoeOutExpress;
    }
}
