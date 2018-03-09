package com.mmall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhengb on 2018-03-05.
 */

/**
 * json序列化和反序列化 使用fatsJson实现
 */
@Slf4j
public class FastJsonUtil {

    /**
     * 对象序列化为json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2JsonStr(T obj) {
        if (obj == null) {
            return null;
        }

        return obj instanceof String ? (String) obj :
                JSON.toJSONString(obj, SerializeConfig.getGlobalInstance(),
                        SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 对象序列化为json字符串 pretty格式
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2PrettyJsonStr(T obj) {
        if (obj == null) {
            return null;
        }

        return obj instanceof String ? (String) obj :
                JSON.toJSONString(obj, SerializeConfig.getGlobalInstance(),
                        SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat);
    }

    /**
     * json反序列化为普通pojo
     * 不可以反序列化为List，因为序列化时类型会被擦除，反序列化后List中的元素类型是HashMap
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonstr2Object(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }

        return JSON.parseObject(str, clazz);
    }

    /**
     * json反序列化为对象列表集合
     * 使用示例
     * <Code>
     * TypeReference<List<User>> typeReference = new TypeReference<List<User>>(){};
     * List<User> userlist = FastJsonUtil.jsonstr2Object(userliststr,  typeReference);
     * </Code>
     * 解决泛型被擦除的问题
     * @param str
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T jsonstr2Object(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        return JSON.parseObject(str, typeReference);
    }

    /**
     * json反序列化为对象列表集合
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonstr2ObjectList(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        return JSON.parseArray(str, clazz);
    }


    public static void main(String[] args) {

        User user1 = new User();
        user1.setUsername("zhangyi");
        user1.setPassword("12222");
        user1.setUpdateTime(new Date());
        user1.setCreateTime(new Date());
        user1.setPhone("135000000");

        User user2 = new User();
        user2.setUsername("zhangyi");
        user2.setPassword("12222");
        user2.setUpdateTime(new Date());
        user2.setCreateTime(new Date());
        user2.setPhone("135000000");

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        String user1JosnStr = FastJsonUtil.obj2JsonStr(user1);
        System.out.println(user1JosnStr);

        TypeReference<User> userTypeReference = new TypeReference<User>(){};

        User user = FastJsonUtil.jsonstr2Object(user1JosnStr,userTypeReference);
        System.out.println(user);

        String  userListStr = FastJsonUtil.obj2JsonStr(userList);
        TypeReference<List<User>> listTypeReference = new TypeReference<List<User>>(){};

        List<User> userList1 = FastJsonUtil.jsonstr2Object(userListStr,listTypeReference);
        System.out.println(userList1);

        //User中不存在location属性时也可以正常反序列化
        String jsonstr = "{\"username\":\"zhangyi\",\"location\":\"hangzhou\"}";
        User user3 = FastJsonUtil.jsonstr2Object(jsonstr,User.class);
        System.out.println(user3);
}
}