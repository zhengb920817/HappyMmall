package com.mmall.task;

/**
 * Created by zhengb on 2018-03-10.
 */

import com.mmall.common.Const;
import com.mmall.common.RedissonManage;
import com.mmall.service.IOrderService;
import com.mmall.service.IRedisPoolService;
import com.mmall.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 定时关闭订单
 */
@Component
@Slf4j
public class CloseOderTask {

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private IRedisPoolService iRedisPoolService;

    @Autowired
    private RedissonManage redissonManage;

    /**
     * 每隔一分钟执行一次关闭
     */
    //@Scheduled(cron = "0 */1 * * * ?")
//    public void closeOrderById(){
//        log.info("关闭订单开始");
//        int hour = Integer.parseInt(PropertiesUtils.getPropertyValue("close.order.task.time.hour","2"));
//        iOrderService.closeOrder(hour);
//        log.info("关闭订单结束");
//    }

    //@Scheduled(cron = "0 */1 * * * ?")
//    public void closerOrderV2(){
//        log.info("关闭订单定时任务启动...");
//        int lockTimeOut = Integer.parseInt(
//                PropertiesUtils.getPropertyValue("lock.timeout","5000"));
//
//        Long setnxResult = iRedisPoolService.setnx(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK,
//                String.valueOf(System.currentTimeMillis() + lockTimeOut));
//
//        if (setnxResult == null || setnxResult == 1) {
//            //返回值1，代表设置锁成功，获取锁
//            closeOrder(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
//        } else {
//            //未获取到锁，继续判断，判断时间戳，看是否可以充值并获取到锁
//            String lockValue = iRedisPoolService.get(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
//            if (lockValue != null && System.currentTimeMillis() > Long.parseLong(lockValue)) {
//                String getSetResult = iRedisPoolService.getSet(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK,
//                        String.valueOf(System.currentTimeMillis() + lockTimeOut));
//                if (getSetResult == null || StringUtils.equals(lockValue, getSetResult)) {
//                    //真正获取到锁
//                    closeOrder(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
//                } else {
//                    log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
//                }
//            }
//            else {
//                log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
//            }
//        }
//        log.info("关闭订单定时任务结束...");
//    }

    @Scheduled(cron = "0 */1 * * * ?")
    private void closerOrderUseRedisson() {
        RLock rLock = redissonManage.getRedisson().getLock(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
        boolean getLock = false;
        try {
            getLock = rLock.tryLock(2, 5, TimeUnit.SECONDS);
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

//    private void closeOrder(String lockName) {
//        iRedisPoolService.expire(lockName, 5);
//        log.info("获取:{}，ThreadName:{}", lockName, Thread.currentThread().getName());
//        int hour = Integer.parseInt(PropertiesUtils.getPropertyValue("close.order.task.time.hour", "2"));
//        //iOrderService.closeOrder(hour);
//        iRedisPoolService.delKey(lockName);
//        log.info("释放：{}，ThreadName:{}", lockName, Thread.currentThread().getName());
//        log.info("=================================");
//    }

}
