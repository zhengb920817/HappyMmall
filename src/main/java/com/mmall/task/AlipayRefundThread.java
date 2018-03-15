package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedisShardePool;
import com.mmall.common.pay.RefundInfo;
import com.mmall.service.IPayService;
import com.mmall.util.FastJsonUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhengb on 2018-03-14.
 * @author zhengb
 * 执行退款线程
 */
@Slf4j
public class AlipayRefundThread implements Runnable {

    private RefundInfo refundInfo;

    private IPayService payService;

    @Override
    public void run() {
        log.info("进入支付宝退款线程，退款信息：{}", refundInfo);
        boolean result = payService.trade_Refund(refundInfo);
        if (result) {
            //写入到redis中
            log.info("退款成功，写入退款信息至redis");
            RedisShardePool.getShardedJedis().lpush(Const.REDIS_KEY.REDIS_KEY_REFUNDREFUNDLIST,
                    FastJsonUtil.obj2JsonStr(refundInfo));
        }
    }

    public AlipayRefundThread(RefundInfo refundInfo, IPayService payService){
        this.refundInfo = refundInfo;
        this.payService = payService;
    }
}
