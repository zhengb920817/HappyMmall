package com.mmall.common;

import com.mmall.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by zhengb on 2018-03-12.
 * @author zhengb
 */
@Component
@Slf4j
public class RedissonManage {
    private Config config = new Config();
    private Redisson redisson = null;
    private static String redisIp1;
    private static Integer redisPort1;

    private static String redisIp2;
    private static Integer redisPort2;

    static {
        redisIp1 = PropertiesUtils.getPropertyValue("redis.ip_1");
        redisPort1 = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.port_1"));

        redisIp2 = PropertiesUtils.getPropertyValue("redis.ip_2");
        redisPort2 = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.port_2"));
    }

    @PostConstruct
    private void init() {
        log.info("开始初始化redisson");
        try {
            config.useSingleServer().setAddress(
                    new StringBuilder().append(redisIp1).append(":").append(redisPort1).toString());

            redisson = (Redisson) Redisson.create(config);
        } catch (Exception e) {
            log.error("初始化Redisson异常{}", e);
        }

        log.info("结束初始化redisson");
    }

    public Redisson getRedisson() {
        return redisson;
    }
}
