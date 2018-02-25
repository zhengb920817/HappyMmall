package com.mmall.common;

/**
 * Created by zhengb on 2018-02-11.
 */
public enum PayResultEnum {
    SUCCESS(1,"支付宝预下单成功"),FAILED(2,"支付宝预下单失败"),UNKNOW(3,"系统异常，预下单状态未知!!!"),
    UNSURPPORT(4,"不支持的交易状态，交易返回异常!!!");

    private PayResultEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }
}
