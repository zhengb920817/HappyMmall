package com.mmall.service;

/**
 * Created by zhengb on 2018-03-07.
 */

/**
 * redis连接池服务，用于redis的操作
 */
public interface IRedisPoolService {

    /**
     * 设置一个key和value
     * @param key
     * @param value
     * @return
     */
    String set(String key, String value);

    /**
     * 根据key获取存储的value
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 设置key和value并制定过期时间
     *
     * @param key
     * @param value
     * @param expireTime 单位是秒
     * @return
     */
    String setex(String key, String value, int expireTime);


    /**
     * 设置key的有效期
     * @param key
     * @param expireTime 单位是秒
     * @return
     */
    Long expire(String key,int expireTime);

    /**
     * 删除一个key
     * @param key
     * @return
     */
    Long delKey(String key) ;

    Long setnx(String key, String value) ;

    String getSet(String key, String value);
}
