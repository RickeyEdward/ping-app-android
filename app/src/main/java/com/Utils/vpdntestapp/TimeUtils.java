package com.Utils.vpdntestapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

  /**
   * 默认日期时间格式: yyyy-MM-dd HH:mm:ss
   */
  public static final String DATE_TIME_PATTERN = "yyyyMMdd-HHmmss";
  public static final String TIME_PATTERN = "HH:mm:ss";

  /**
   * 获取当前时间
   */
  public static String getTime() {
    Date date = new Date();
    return convertDateToString(date, TIME_PATTERN);
  }

  /**
   * 获取当前日期时间
   */
  public static String getDate() {
    Date date = new Date();
    return convertDateToString(date, DATE_TIME_PATTERN);
  }

  /**
   * 常用的格式化日期
   *
   * @param date Date
   * @return String
   */
  public static String convertDateToString(Date date) {
    return convertDateToString(date, "yyyy-MM-dd");
  }

  /**
   * 格式化日期
   *
   * @param dateStr 字符型日期
   * @param formatStr 格式
   * @return 返回日期
   */
  public static Date convertStringToDate(String dateStr, String formatStr) {
    SimpleDateFormat format = new SimpleDateFormat(formatStr);
    try {
      return format.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 以指定的格式来格式化日期
   *
   * @param date Date
   * @param format String
   * @return String
   */
  public static String convertDateToString(Date date, String format) {
    String result = "";
    if (date != null) {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        result = sdf.format(date);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return result;
  }

}
