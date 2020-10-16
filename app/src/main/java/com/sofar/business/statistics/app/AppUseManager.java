package com.sofar.business.statistics.app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;

import com.sofar.BuildConfig;
import com.sofar.SofarApp;
import com.sofar.base.app.AppLifeManager;
import com.sofar.utility.DateUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AppUseManager {

  private static String TAG = "AppUseManager";

  UsageStatsManager usm;

  /**
   * 按天存储app使用信息
   */
  private Map<String, List<UsageStats>> dayUsageStatsMap = new TreeMap<>();

  private boolean updating;

  private static class Inner {
    static AppUseManager INSTANCE = new AppUseManager();
  }

  public static AppUseManager get() {
    return Inner.INSTANCE;
  }

  private AppUseManager() {
    usm = (UsageStatsManager) SofarApp.getAppContext().getSystemService(Context.USAGE_STATS_SERVICE);
  }

  /**
   * @param day 最近n天的app使用信息
   */
  public void updateAppUseRecent(int day) {
    if (updating) {
      return;
    }

    updating = true;
    queryAppUseRecent(day).subscribe(map -> {
      updating = false;
    }, throwable -> {
      updating = false;
    });
  }

  public Observable<Map<String, List<UsageStats>>> queryAppUseRecent(final int day) {
    return Observable.fromCallable((Callable<Map<String, List<UsageStats>>>) () -> {
      long startTime = DateUtil.getTodayZeroTime() - day * DateUtils.DAY_IN_MILLIS;
      long endTime = DateUtil.getTodayEndTime();
      Log.d(TAG, "startTime=" + DateUtil.getTime(startTime) + " endTime=" + DateUtil.getTime(endTime));
      List<UsageStats> usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
      if (usageStats.size() == 0) {
        return new HashMap<>();
      }

      dayUsageStatsMap.clear();
      for (UsageStats item : usageStats) {
        if (item.getTotalTimeInForeground() / DateUtils.MINUTE_IN_MILLIS > 0) {
          String firstTime = DateUtil.getTime(item.getFirstTimeStamp(), DateUtil.DAY_DATE_FORMAT);
          List<UsageStats> list = dayUsageStatsMap.get(firstTime);
          if (list == null) {
            list = new ArrayList<>();
            dayUsageStatsMap.put(firstTime, list);
          }
          list.add(item);
        }
      }
      return dayUsageStatsMap;
    }).doOnNext(map -> {
      if (BuildConfig.DEBUG) {
        for (Map.Entry<String, List<UsageStats>> entry : map.entrySet()) {
          String key = entry.getKey();
          List<UsageStats> value = entry.getValue();
          Log.d(TAG, "------" + key + "------");
          for (UsageStats item : value) {
            printLog(item);
          }
        }
      }
    }).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  private void printLog(UsageStats item) {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    sb.append("包名=" + item.getPackageName());
    sb.append("应用名=" + AppUtil.getAppName(item.getPackageName()));
    sb.append("第一次启动时间=" + DateUtil.getTime(item.getFirstTimeStamp()));
    sb.append("最后一次启动时间=" + DateUtil.getTime(item.getLastTimeStamp()));
    sb.append("前台总运行时间=" + item.getTotalTimeInForeground() / DateUtils.MINUTE_IN_MILLIS + "min");
    sb.append("启动次数=" + getLaunchCount(item));
    sb.append("]");
    Log.d(TAG, sb.toString());
  }

  private int getLaunchCount(UsageStats usageStats) {
    Field field = null;
    try {
      field = usageStats.getClass().getDeclaredField("mLaunchCount");
      return (int) field.get(usageStats);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * 存储最近7天 的app使用信息
   */
  public Map<String, List<UsageStats>> getDayUsageStatsMap() {
    return dayUsageStatsMap;
  }

  /**
   * 是否有查看应用使用情况的权限
   */
  public boolean hasPermission() {
    long endTime = System.currentTimeMillis();
    long startTime = DateUtil.getDateBefore(endTime, 1);
    List<UsageStats> usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
    return usageStats.size() > 0;
  }

  /**
   * 打开设置页面，添加权限
   */
  public void startSettingPage() {
    AppLifeManager.get().getCurrentActivity().startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
  }

}
