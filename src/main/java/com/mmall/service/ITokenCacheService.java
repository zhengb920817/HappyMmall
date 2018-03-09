package com.mmall.service;

/**
 * Created by zhengb on 2018-03-06.
 */
public interface ITokenCacheService {
    void setTokenValue(String username,String value);

    String getTokenValue(String username);
}
