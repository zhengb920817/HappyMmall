package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by zhengb on 2018-01-21.
 * @author zhengb
 * 全局常量定义
 */
public class Const {
    public static final String CURRENT_USER = "current_User";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public static final String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";

    public static final String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";

    public interface RedisCacheExTime{
        /**
         * Redis缓存有效时间，设置为30分钟
         */
        int REDIS_SESSION_EXTIME = 60 * 30;
    }

    /**
     * 用户类型
     */
    public enum RegRole{
        CUSTOMER(0,"普通用户"),ADMIN(1,"管理员");

        private int userType;
        private String desc;

        RegRole(int userType,String desc){
            this.userType = userType;
            this.desc = desc;
        }

        public int getUserType(){
            return userType;
        }

        public String getDesc(){
            return desc;
        }
    }

    public interface ProductListOrderBy{
        Set<String> RRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    /**
     * 商品在售状态
     */
    public enum ProductStatusEnum{
        ON_SALE(1,"在售")
        ;

        private String value;
        private int code;

        ProductStatusEnum(int code, String value){
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 商品选中状态
     */
    public enum CartStaus {
        CHECKED(1, "选中状态"),
        UNCHECKED(0, "非选中状态");

        CartStaus(int code, String desc){
            this.code = code;
            this.desc = desc;
        }

        private final int code;
        private final String desc;

        public int getCode(){
            return code;
        }

        public String getDesc(){
            return desc;
        }
    }

    /**
     * 订单状态枚举
     */
    public enum OrderStatusEnum {
        Cancel(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已支付"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_REFUNDING(60,"退款中"),
        ORDER_REFUND(70, "已退款"),
        ORDER_CLOSED(80, "订单关闭");

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum:values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("不支持的订单状态");
        }
    }

    public interface AliPayCallBack{
        /**
         * 等待支付
         */
        String TRADE_STUAS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        /**
         * 交易成功
         */
        String TRADE_STUAS_TRADE_SUCCESS = "TRADE_SUCCESS";
        /**
         * 退款中
         */
        String TRADE_STUAS_TRADE_REFUNDING = "TRADE_REFUNDING";

        /**
         * 退款成功
         */
        String TRADE_STUAS_TRADE_REFUND_SUCCESS = "TRADE_REFUND_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    /**
    支付平台枚举
     */
    public enum PayPlatformEnum{
       ALIPAY(1,"支付宝"),;

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 支付类型枚举
     */
    public enum PaymentTypeEnum{
        ONLINE(1,"在线支付"),;

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code)
                {
                    return paymentTypeEnum;
                }
            }

            throw new RuntimeException("没有找到支付类型对应的枚举");
        }
    }

    public interface REDIS_LOCK{
        String CLOSER_ORDER_TASK_LOCK = "CLOSER_ORDER_TASK_LOCK";
        String REFUND_ORDER_TASK_LOCK = "REFUND_ORDER_TASK_LOCK";
    }

    public interface REDIS_KEY{
        String REDIS_KEY_REFUNDREFUNDLIST = "REFUNDLIST";
    }
}
