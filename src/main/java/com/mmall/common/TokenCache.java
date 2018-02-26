package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhengb on 2018-01-29.
 */
public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static String TOKEN_PREFIX = "Token_";
    //LRU算法
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().
            initialCapacity(1000).maximumSize(10000).expireAfterAccess(12,TimeUnit.HOURS)
            .build(
                    //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法获取
                    new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) throws Exception {
                            return "null";
                        }
                    }
            );

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if(value.equals("null")){
                return  null;
            }
           return value;
        }catch(Exception e) {
            logger.error("localCache  getError" + e);
            return null;
        }
    }
}
