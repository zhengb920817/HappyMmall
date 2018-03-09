package com.mmall.util;

import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhengb on 2018-03-05.
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认的timestamp形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);
        //忽略空bean转json错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //所有的日期格式统一为"yyyy-MM-dd HH:mm:ss"
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STAND_FORMATTER));

        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String object2String(T obj) {

        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("object2String parse error " + e) ;
            return null;
        }
    }

    public static <T> String object2PrettyString(T obj) {

        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (IOException e) {
            log.error("object2PrettyString parse error " + e);
            return null;
        }
    }


    public static <T> T string2Obj(String str, Class<T> claszz) {
        if(StringUtils.isEmpty(str) || claszz == null){
            return null;
        }

        try {
            return claszz == String.class ? (T)str : objectMapper.readValue(str,claszz);
        } catch (IOException e) {
            log.error("string2Obj parse error " + e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }

        try {
            return (T) (typeReference.getType().equals(String.class) ?
                    str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            log.error("string2Obj parse error " + e);
            return null;
        }
    }

    /**
     * 字符串反序列化为对象
     * @param str json字符串
     * @param collectionClass 集合的类,如List.class
     * @param elementClasses 集合中元素类型 如User.class
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,
                elementClasses);

        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.error("string2Obj parse error " + e);
            return null;
        }
    }


    public static void main(String[] args) {

        User user = new User();
        user.setUsername("zhangyi");
        user.setPhone("13588056736");
        user.setEmail("461426598@qq.com");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setPassword("123456");
        user.setId(1);
        user.setQuestion("wenti");
        user.setAnswer("daan");
        String userStr = object2String(user);
        System.out.println(userStr);

        String prettyStr = object2PrettyString(user);
        System.out.println(prettyStr);

        user = string2Obj(userStr,User.class);
        System.out.println(user);

        User user2 = new User();
        user2.setUsername("zhangyi");
        user2.setPhone("13588056736");

        List<User> list = new ArrayList<>();

        list.add(user);
        list.add(user2);

        String str = object2String(list);

        //List<User> userlistobj = string2Obj(str,List.class);
        List<User> userlistobj = string2Obj(str, new TypeReference<List<User>>() {

        });
        System.out.println(userlistobj);

        List<User> userListobj2 = string2Obj(str,List.class,User.class);
        System.out.println(userListobj2);
    }

}
