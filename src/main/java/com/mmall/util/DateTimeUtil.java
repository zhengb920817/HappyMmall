package com.mmall.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by zhengb on 2018-02-03.
 */
public class DateTimeUtil {

    public final static String STAND_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    //joda-time
    /*
    字符串转换为日期
     */
    public static Date strToDate(String dateTimeStr, String formatStr){
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern(formatStr);

        DateTime dateTime = timeFormat.parseDateTime(dateTimeStr);

        return dateTime.toDate();
    }

    /*
    日期转换为字符串
     */
    public static String dateToStr(Date date, String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    /*
    日期转换为字符串 使用默认格式
     */
    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STAND_FORMATTER);
    }

    /*
    字符串转换为日期 使用默认格式
     */
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern(STAND_FORMATTER);

        DateTime dateTime = timeFormat.parseDateTime(dateTimeStr);

        return dateTime.toDate();
    }

    public static LocalDateTime convertStrToTime(String dateTimeStr){
       return LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern(STAND_FORMATTER));
    }

    public static void main(String[] args) {
        Date date = strToDate("2018-02-03 17:02:01","yyyy-MM-dd HH:mm:ss");
        System.out.println(date);

        String str = dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss");
        System.out.println(str);
    }
}
