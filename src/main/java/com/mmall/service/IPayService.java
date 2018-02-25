package com.mmall.service;

import com.mmall.common.AliPayInfo;
import com.mmall.common.PayResult;

/**
 * Created by zhengb on 2018-02-11.
 */
public interface IPayService {
    PayResult trade_precreate(AliPayInfo aliPayInfo);
}
