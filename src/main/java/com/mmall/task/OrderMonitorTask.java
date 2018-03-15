package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManage;
import com.mmall.common.pay.RefundInfo;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.service.IOrderService;
import com.mmall.service.IRedisPoolService;
import com.mmall.util.FastJsonUtil;
import com.mmall.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhengb on 2018-03-14.
 * @author zhengb
 * 订单监控定时任务
 * 用于关闭2小时仍未支付的订单
 * 从reids的退款列表中取出退款信息并更新数据库
 */
@Slf4j
@Component
public class OrderMonitorTask {

    private  static  final String CRON_EXP = "0 */1 * * * ?";
    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private RedissonManage redissonManage;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    /**
     * 每隔一分钟执行一次关闭
     * 关闭两小时仍未付款的订单
     */
    @Scheduled(cron = CRON_EXP)
    public void closerOrderUseRedisson() {

        RLock rLock = redissonManage.getRedisson().getLock(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
        boolean getLock = false;
        try {
            getLock = rLock.tryLock(0, 50, TimeUnit.SECONDS);
            if (getLock) {
                log.info("Redisson获取分布式锁：{}，ThreadName：{}", Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK,
                        Thread.currentThread().getName());
                int hour = Integer.parseInt(PropertiesUtils.getPropertyValue(
                        "close.order.task.time.hour", "2"));
                iOrderService.closeOrder(hour);
            } else {
                log.info("Redisson获取分布式锁失败：{}，ThreadName：{}", Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK,
                        Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.info("获取redisson锁异常{}", e);
        } finally {
            if (getLock) {
                rLock.unlock();
                log.info("Redisson分布式锁释放锁！");
            }
        }
    }


    /**
     * 更新已退款的订单
     */
    @Scheduled(cron = CRON_EXP)
    public void updateOrderRefund() {
        RLock rLock = redissonManage.getRedisson().getLock(Const.REDIS_LOCK.REFUND_ORDER_TASK_LOCK);
        boolean getLock = false;
        try {
            getLock = rLock.tryLock(0, 100, TimeUnit.SECONDS);
            if (getLock) {
                log.info("Redisson获取分布式锁：{}，ThreadName：{}", Const.REDIS_LOCK.REFUND_ORDER_TASK_LOCK,
                        Thread.currentThread().getName());

                String refundInfoStr = null;
                //从退款队列中不断获取退款信息
                while ((refundInfoStr = iRedisPoolService.rpop(Const.REDIS_KEY.REDIS_KEY_REFUNDREFUNDLIST)) != null) {
                    RefundInfo refundInfo = FastJsonUtil.jsonstr2Object(refundInfoStr, RefundInfo.class);
                    if (refundInfo != null) {
                        String orderNo = refundInfo.getOutTradNo();
                        Integer userId = refundInfo.getUserId();
                        orderMapper.updateOrderStatusByOrderNoAndUserId(userId, Long.parseLong(orderNo),
                                Const.OrderStatusEnum.ORDER_REFUND.getCode());
                        payInfoMapper.updatePlatformStatusByOrderNoAndUserId(userId, Long.parseLong(orderNo),
                                Const.AliPayCallBack.TRADE_STUAS_TRADE_REFUND_SUCCESS);
                    }
                }
            } else {
                log.info("Redisson获取分布式锁失败：{}，ThreadName：{}", Const.REDIS_KEY.REDIS_KEY_REFUNDREFUNDLIST,
                        Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.info("获取redisson锁{}异常{}", Const.REDIS_KEY.REDIS_KEY_REFUNDREFUNDLIST, e);
        } finally {
            rLock.unlock();
            log.info("Redisson分布式锁{}释放锁！", Const.REDIS_KEY.REDIS_KEY_REFUNDREFUNDLIST);
        }
    }
}
