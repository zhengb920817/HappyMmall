package com.mmall.task;

/**
 * Created by zhengb on 2018-03-10.
 */

import com.mmall.service.IOrderService;
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

    /**
     * 每隔一分钟执行一次关闭
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderById(){
        log.info("关闭订单开始");
        int hour = Integer.parseInt(PropertiesUtils.getPropertyValue("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单结束");
    }

}
