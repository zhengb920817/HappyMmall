package com.mmall.service.impl;

import com.mmall.service.IRedisPoolService;
import com.mmall.service.ITokenCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhengb on 2018-03-06.
 */
@Service
@Slf4j
public class TokenCacheServiceImp implements ITokenCacheService{

    @Autowired
    private IRedisPoolService iRedisPoolService;

    private static String TOKEN_PREFIX = "Token_";

    @Override
    public void setTokenValue(String username, String value) {
        log.info("setTokenValue写入token至cache，name:{},value：{}", TOKEN_PREFIX + username, value);
        iRedisPoolService.setex(TOKEN_PREFIX + username, value, 60 * 30 * 12);
    }

    @Override
    public String getTokenValue(String username) {
        return iRedisPoolService.get(TOKEN_PREFIX + username);
    }
}
