package com.mmall.common;

/**
 * Created by zhengb on 2018-02-11.
 * 支付结果返回信息
 */
public class PayResult<T>{
    /*
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

    public static <T> PayResult createPayResultSuccessMessage(T respMsg, PayResultEnum resultEnum) {
        return new PayResult(respMsg, resultEnum);
    }

    public static PayResult createPayResultFailMessage(PayResultEnum resultEnum){
        return new PayResult(resultEnum.getDesc(), resultEnum);
    }
}
