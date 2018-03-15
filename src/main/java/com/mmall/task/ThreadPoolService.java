package com.mmall.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengb on 2018-03-14.
 * @author zhengb
 * 全局线程池服务
 */
@Slf4j
@Component
public class ThreadPoolService {

    /**
     * 退款线程工厂
     */
    private static ThreadFactory RefundThreadFactory;

    /**
     * 退款线程池
     */
    private static ThreadPoolExecutor reFundThreadPoolExecutor;

    static {
        RefundThreadFactory = new ThreadFactoryBuilder().
                setNameFormat("refund-threadpool-%d").setPriority(
                Thread.NORM_PRIORITY).build();
        reFundThreadPoolExecutor = new ThreadPoolExecutor(10, 200,
                0L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), RefundThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }


    public ThreadPoolExecutor getReFundThreadPoolExecutor() {
        return reFundThreadPoolExecutor;
    }

    @PreDestroy
    public void shutDownThreadPool(){
        log.info("开始关闭线程池服务...");
        if(reFundThreadPoolExecutor != null){
            reFundThreadPoolExecutor.shutdown();
            try {
                reFundThreadPoolExecutor.awaitTermination(1,TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("关闭线程池异常，{}",e);
            }
        }
        log.info("结束关闭线程池");
    }
}
