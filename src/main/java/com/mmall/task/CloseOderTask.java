package com.mmall.task;

/**
 * Created by zhengb on 2018-03-10.
 */

import com.mmall.common.Const;
import com.mmall.service.IOrderService;
import com.mmall.service.IRedisPoolService;
import com.mmall.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    /**
     * 每隔一分钟执行一次关闭
     */
    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderById(){
        log.info("关闭订单开始");
        int hour = Integer.parseInt(PropertiesUtils.getPropertyValue("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单结束");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closerOrderV2(){
        log.info("关闭订单定时任务启动...");
        int lockTimeOut = Integer.parseInt(
                PropertiesUtils.getPropertyValue("lock.timeout","50000"));

        Long setnxResult = iRedisPoolService.setnx(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeOut));

        if(setnxResult == null || setnxResult == 1){
            //返回值1，代表设置锁成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
        }
        else {
            log.info("没有获取到分布式锁:{}",Const.REDIS_LOCK.CLOSER_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束...");
    }

    public void closeOrder(String lockName) {
        iRedisPoolService.expire(lockName, 50);
        log.info("获取:{}，ThreadName:{}", lockName, Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtils.getPropertyValue("close.order.task.time.hour", "2"));
        iOrderService.closeOrder(hour);
        iRedisPoolService.delKey(lockName);
        log.info("释放：{}，ThreadName:{}", lockName, Thread.currentThread().getName());
    }

}
