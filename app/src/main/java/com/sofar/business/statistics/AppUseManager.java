package com.sofar.business.statistics;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.sofar.SofarApp;
import com.sofar.base.app.AppLifeManager;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

public class AppUseManager {

  private static String TAG = "AppUseManager";

  UsageStatsManager usm;

  private static class Inner {
    static AppUseManager INSTANCE = new AppUseManager();
  }

  public static AppUseManager get() {
    return Inner.INSTANCE;
  }

  private AppUseManager() {
    usm = (UsageStatsManager) SofarApp.getAppContext().getSystemService(Context.USAGE_STATS_SERVICE);
  }

  public void getAppUse() {
    Calendar calendar = Calendar.getInstance();
    long endTime = calendar.getTimeInMillis();
    calendar.add(Calendar.DAY_OF_WEEK, -2);
    long startTime = calendar.getTimeInMillis();
    List<UsageStats> usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

    if (usageStats.size() == 0) {
      startSettingPage();
    } else {
      for (UsageStats item : usageStats) {
        if (item.getTotalTimeVisible() > 0) {
          printLog(item);
        }
      }
    }
  }

  private void printLog(UsageStats item) {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    sb.append("包名=" + item.getPackageName());
    sb.append("第一次启动时间=" + item.getFirstTimeStamp());
    sb.append("最后一次启动时间=" + item.getLastTimeStamp());
    sb.append("前台总运行时间=" + item.getTotalTimeVisible());
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

  private void startSettingPage() {
    AppLifeManager.get().getCurrentActivity().startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
  }

}
