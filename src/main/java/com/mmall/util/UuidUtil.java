package com.mmall.util;

import java.util.UUID;

/**
 * Created by zhengb on 2018-03-09.
 */
public class UuidUtil {

    public static String getUUIDString() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }
}
