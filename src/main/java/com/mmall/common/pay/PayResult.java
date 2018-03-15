package com.mmall.common.pay;

/**
 * Created by zhengb on 2018-02-11.
 * 支付宝处理结果返回
 * @author zhengb
 */
public class PayResult<T>{
    /**
    返回消息体
     */
    private T respMsg;

    private PayResultEnum tradeStatus;

    public T getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(T respMsg) {
        this.respMsg = respMsg;
    }

    public PayResultEnum getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(PayResultEnum tradeStatus) {
        this.tradeStatus = tradeStatus;
    }


    private PayResult(T respMsg, PayResultEnum tradeStatus){
        this.respMsg = respMsg;
        this.tradeStatus = tradeStatus;
    }
    
    private PayResult(PayResultEnum tradeStatus){
        this.tradeStatus = tradeStatus;
    }

    public static <T> PayResult<T> createPayResultSuccessMessage(T respMsg) {
        return new PayResult<T>(respMsg, PayResultEnum.SUCCESS);
    }

    public static <T> PayResult<T> createPayResultFailMessage(PayResultEnum resultEnum){
        return new PayResult<T>(resultEnum);
    }
}
