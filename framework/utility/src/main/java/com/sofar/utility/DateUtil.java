package com.sofar.utility;


import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

  public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final SimpleDateFormat DAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private DateUtil() {
    throw new AssertionError();
  }

  /**
   * long time to string
   *
   * @param timeInMillis
   * @param dateFormat
   * @return
   */
  public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
    return dateFormat.format(new Date(timeInMillis));
  }

  /**
   * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
   *
   * @param timeInMillis
   * @return
   */
  public static String getTime(long timeInMillis) {
    return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
  }

  /**
   * get current time in milliseconds
   *
   * @return
   */
  public static long getCurrentTimeInLong() {
    return System.currentTimeMillis();
  }

  /**
   * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
   *
   * @return
   */
  public static String getCurrentTimeInString() {
    return getTime(getCurrentTimeInLong());
  }

  /**
   * get current time in milliseconds
   *
   * @return
   */
  public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
    return getTime(getCurrentTimeInLong(), dateFormat);
  }

  /**
   * 获取距当前时间 n天的时间
   */
  public static long getCurrentTimeAfterDays(int day) {
    return System.currentTimeMillis() + day * 24 * 60 * 60 * 1000;
  }

  /**
   * 获取给定time时间 几天前的时间
   */
  public static long getDateBefore(long time, int day) {
    Calendar now = Calendar.getInstance();
    now.setTimeInMillis(time);
    now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
    return now.getTimeInMillis();
  }

  /**
   * 获取给定time时间 几天后的时间
   */
  public static long getDateAfter(long time, int day) {
    Calendar now = Calendar.getInstance();
    now.setTimeInMillis(time);
    now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
    return now.getTimeInMillis();
  }

  /**
   * 今天0点的时间戳
   */
  public static long getTodayZeroTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar.getTimeInMillis();
  }

  /**
   * 今天23点59分59秒的毫秒数
   */
  public static long getTodayEndTime() {
    return getTodayZeroTime() + DateUtils.DAY_IN_MILLIS - 1000;
  }

}
