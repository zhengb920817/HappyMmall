package com.mmall.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Created by zhengb on 2018-03-11.
 */
@Aspect
@Component
@Slf4j
public class RedisLogAspect {

    @Pointcut("execution(public * com.mmall.service.impl.ShardeRedisPoolServiceImp.*(..))")
    public void logPointCut() {
    }

    @Before("logPointCut()")
    public void beforeLog(JoinPoint joinPoint) {
        log.info("redis: method:{},args:{}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @After("logPointCut()")
    public void afterLog() {
        log.info("redis after");
    }

    @AfterReturning(pointcut = "logPointCut()", returning = "object")
    public void afterReturningLog(Object object) {
        log.info("redis return:{}",
                object != null ? object.toString() : "null");
    }

    @AfterThrowing(pointcut = "logPointCut()", throwing = "exception")
    public void errorLog(Exception exception) {
        log.error("redis error{}", exception.getMessage());
    }

}
