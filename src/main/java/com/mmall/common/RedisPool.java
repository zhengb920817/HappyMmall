package com.mmall.common;

import com.mmall.util.PropertiesUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhengb on 2018-03-05.
 */

/**
 * redis连接池
 */
public class RedisPool {
    /**
     * jedis连接池
     */
    private static JedisPool jedisPool;
    /**
     * 最大连接数
     */
    private static Integer maxTotal;

    /**
     * 在jedispool中最大的idle状态(空闲状态)的jedis实例个数
     */
    private static Integer maxIdle;

    /**
     * 在jedispool中最小的idle状态(空闲状态)的jedis实例个数
     */
    private static Integer minIdle;

    /**
     * 在borrow一个jedis实例时，是否要进行验证操作，如果设置为true，则得到的jedis实例肯定是可以用的
     */
    private static Boolean testOnBorrow;
    /**
     * 在return一个jedis实例时，是否要进行验证操作，如果设置为true，则放回jedispool的jedis实例肯定是可以用的
     */
    private static Boolean testOnReturn;

    /**
     * redis连接ip
     */
    private static String redisIp;

    /**
     * redis连接端口
     */
    private static Integer redisPort;

    private static Lock resourceLock = new ReentrantLock();

    private static void initializePool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        //连接耗尽时，是否阻塞 如果设置为false，连接耗尽时会抛出异常，true阻塞直到超时，默认为true
        poolConfig.setBlockWhenExhausted(true);

        jedisPool = new JedisPool(poolConfig, redisIp, redisPort, 1000 * 2);
    }

    static {
        /**
         * 先初始化参数
         */
        maxTotal = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.max.total", "20"));
        maxIdle = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.max.idle","10"));
        minIdle = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.min.idle","2"));
        testOnBorrow = Boolean.parseBoolean(PropertiesUtils.getPropertyValue("redis.test.borrow","true"));
        testOnReturn = Boolean.parseBoolean(PropertiesUtils.getPropertyValue("redis.test.return","true"));
        redisIp = PropertiesUtils.getPropertyValue("redis.ip");
        redisPort = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.port"));
    }

    static {
        /**
         * 根据参数初始化连接池
         */
        initializePool();
    }

    public static Jedis getJedis() {
        resourceLock.lock();
        try {
            return jedisPool.getResource();
        }
        finally {
            resourceLock.unlock();
        }
    }

    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    public static void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

    public static void main(String[] args) {
        Jedis jedis = getJedis();
        jedis.setnx("key2","zhangyi");
        returnResource(jedis);
        jedisPool.destroy();;
        System.out.print("end");
    }
}
