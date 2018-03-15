package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.mmall.common.pay.*;
import com.mmall.pojo.OrderItem;
import com.mmall.service.IPayService;
import com.mmall.util.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengb on 2018-02-11.
 */
@Service("iPayServce")
@Slf4j
/**
 * 支付宝支付服务实现类
 */
public class PayServiceImp implements IPayService {

    /**支付宝当面付2.0服务
     */
    private static AlipayTradeService tradeService;

    /**
     * 支付宝当面付2.0服务（集成了交易保障接口逻辑）
     */
    private static AlipayTradeService tradeWithHBService;

    /**
     * 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
     */
    private static AlipayMonitorService monitorService;

    static {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    private String dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }

            return response.getBody();
        }
        return null;
    }

    /**
    处理生成支付二维码返回结果
     */
    private PayResult<PayPrecreateResponse> getPayResult(AlipayF2FPrecreateResult result) {
        switch (result.getTradeStatus()) {
            case SUCCESS:
                AlipayTradePrecreateResponse response = result.getResponse();
                String responseBody = dumpResponse(response);
                PayPrecreateResponse precreateResponse = new PayPrecreateResponse();
                precreateResponse.setBody(responseBody);
                precreateResponse.setQrCode(response.getQrCode());
                precreateResponse.setTradeNo(response.getOutTradeNo());
                return PayResult.createPayResultSuccessMessage(precreateResponse);
            case FAILED:
                return PayResult.createPayResultFailMessage(PayResultEnum.FAILED);
            case UNKNOWN:
                return PayResult.createPayResultFailMessage(PayResultEnum.UNKNOW);
            default:
                return PayResult.createPayResultFailMessage(PayResultEnum.UNSURPPORT);
        }
    }

    /**
     * 生成支付二维码
     *
     * @param aliPayInfo
     * @return 支付结果信息
     */
    @Override
    public PayResult<PayPrecreateResponse> trade_precreate(AliPayInfo aliPayInfo) {

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = aliPayInfo.getTradeNo();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = aliPayInfo.getSubject();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = aliPayInfo.getTotalAmout().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = aliPayInfo.getUndiscountAmout().toString();

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = aliPayInfo.getSellerId();

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = aliPayInfo.getBody();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = aliPayInfo.getOpratortId();

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = aliPayInfo.getStoreId();

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = aliPayInfo.getTimoeOutExpress();

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItems = aliPayInfo.getOrderItemList();
        for (OrderItem orderItem : orderItems) {
            GoodsDetail good = GoodsDetail.newInstance(orderItem.getId().toString(),
                    orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),
                            new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(good);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl(aliPayInfo.getCallbackUrl())
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);

        return getPayResult(result);
    }

    @Override
    public boolean trade_Refund(RefundInfo refundInfo) {
        String outTradeNo = refundInfo.getOutTradNo();

        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
        String refundAmount = refundInfo.getRefundAmout().toString();

        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
        String outRequestNo = refundInfo.getOutRequestNo();

        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
        String refundReason = refundInfo.getRefundReason();

        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
        String storeId = refundInfo.getStoreId();

        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(outTradeNo)
                .setRefundAmount(refundAmount)
                .setRefundReason(refundReason)
                .setOutRequestNo(outRequestNo)
                .setStoreId(storeId);

        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝退款成功: )");
                return true;

            case FAILED:
                log.error("支付宝退款失败!!!");
                return false;

            case UNKNOWN:
                log.error("系统异常，订单退款状态未知!!!");
                return false;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return false;
        }
    }

}
