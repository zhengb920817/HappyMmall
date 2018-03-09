package com.mmall.service.impl;

import com.mmall.common.RedisShardePool;
import com.mmall.service.IRedisPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;


/**
 * Created by zhengb on 2018-03-07.
 */

/**
 * 分布式jedis连接池实现
 */
@Slf4j
@Service("shardeRedisPoolServiceImp")
public class ShardeRedisPoolServiceImp implements IRedisPoolService {

    private ShardedJedis getShardeJedisPool(){
        return RedisShardePool.getShardedJedis();
    }

    private void returnBrokenResource(ShardedJedis shardedJedis){
        RedisShardePool.returnBrokenResource(shardedJedis);
    }

    private void returnResource(ShardedJedis shardedJedis){
        RedisShardePool.returnResource(shardedJedis);
    }

    @Override
    public String set(String key, String value) {
        ShardedJedis shardedJedis = null;
        String result = null;

        try {
            shardedJedis = getShardeJedisPool();
            result = shardedJedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            returnBrokenResource(shardedJedis);
            return result;
        }

        returnResource(shardedJedis);
        return result;
    }

    @Override
    public String get(String key) {
        ShardedJedis shardedJedis = null;
        String result = null;

        try {
            shardedJedis = getShardeJedisPool();
            result = shardedJedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            returnBrokenResource(shardedJedis);
            return result;
        }

        returnResource(shardedJedis);
        return result;
    }

    @Override
    public String setex(String key, String value, int expireTime) {
        ShardedJedis shardedJedis = null;
        String result = null;

        try {
            shardedJedis = getShardeJedisPool();
            result = shardedJedis.setex(key, expireTime, value);
        } catch (Exception e) {
            log.error("setex key:{} value:{},expireTime:{} error", key, value, expireTime, e);
            returnBrokenResource(shardedJedis);
            return result;
        }

        returnResource(shardedJedis);
        return result;
    }


    @Override
    public Long expire(String key,int expireTime) {
        ShardedJedis shardedJedis = null;
        Long result = null;

        try {
            shardedJedis = getShardeJedisPool();
            result = shardedJedis.expire(key,expireTime);
        } catch (Exception e) {
            log.error("expire key:{}, expireTime:{} error", key, expireTime, e);
            returnBrokenResource(shardedJedis);
            return result;
        }

        returnResource(shardedJedis);
        return result;
    }

    @Override
    public Long delKey(String key) {
        ShardedJedis shardedJedis = null;
        Long result = null;

        try {
            shardedJedis = getShardeJedisPool();
            result = shardedJedis.del(key);
        } catch (Exception e) {
            log.error("delKey key:{} error", key, e);
            returnBrokenResource(shardedJedis);
            return result;
        }

        returnResource(shardedJedis);
        return result;
    }
}
