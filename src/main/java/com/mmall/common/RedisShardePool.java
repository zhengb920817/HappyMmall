package com.mmall.common;

import com.mmall.util.PropertiesUtils;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分布式jedis连接池
 * @author zhengb
 */
public class RedisShardePool {

    /**
     * 分布式jedis连接池
     */
    private static ShardedJedisPool shardedJedisPool;
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
     * redis连接ip1
     */
    private static String redisIp1;

    /**
     * redis连接端口1
     */
    private static Integer redisPort1;

    /**
     * redis连接ip2
     */
    private static String redisIp2;

    /**
     * redis连接端口2
     */
    private static Integer redisPort2;


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

        JedisShardInfo info1 = new JedisShardInfo(redisIp1, redisPort1, 2000);
        JedisShardInfo info2 = new JedisShardInfo(redisIp2, redisPort2, 2000);

        List<JedisShardInfo> shardInfoList = new ArrayList<>();

        shardInfoList.add(info1);
        shardInfoList.add(info2);

        shardedJedisPool = new ShardedJedisPool(poolConfig,
                shardInfoList, Hashing.MURMUR_HASH,
                Sharded.DEFAULT_KEY_TAG_PATTERN);
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

        redisIp1 = PropertiesUtils.getPropertyValue("redis.ip_1");
        redisPort1 = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.port_1"));

        redisIp2 = PropertiesUtils.getPropertyValue("redis.ip_2");
        redisPort2 = Integer.parseInt(PropertiesUtils.getPropertyValue("redis.port_2"));
    }

    static {
        /**
         * 根据参数初始化连接池
         */
        initializePool();
    }

    public static ShardedJedis getShardedJedis() {
        resourceLock.lock();
        try {
            return shardedJedisPool.getResource();
        }
        finally {
            resourceLock.unlock();
        }
    }

    public static void returnResource(ShardedJedis jedis) {
        if (jedis != null) {
            shardedJedisPool.returnResource(jedis);
        }
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        if (jedis != null) {
            shardedJedisPool.returnBrokenResource(jedis);
        }
    }

    public static void main(String[] args) {
        ShardedJedis jedis = getShardedJedis();
        //jedis.setnx("key2","zhangyi");
        for(int i= 0;i<10;i++){
            jedis.set("key" + i, "value" +i );
        }

        System.out.println( jedis.get("key1"));
        System.out.println( jedis.get("key7"));
        returnResource(jedis);
        //shardedJedisPool.destroy();
        System.out.print("end");
    }
}
