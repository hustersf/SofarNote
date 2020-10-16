package com.sofar.business.statistics.app;

import android.graphics.drawable.Drawable;

public class AppUseInfo implements Comparable<AppUseInfo> {

  public String appName;
  public Drawable appIcon;

  public long useTimeMinutes;
  public int timePercent;

  public String formatTime() {
    int h = (int) (useTimeMinutes / 60);
    int min = (int) (useTimeMinutes % 60);
    StringBuffer sb = new StringBuffer();
    if (h > 0) {
      sb.append(h);
      sb.append("小时");
    }
    sb.append(min);
    sb.append("分钟");
    return sb.toString();
  }

  @Override
  public int compareTo(AppUseInfo o) {
    return useTimeMinutes > o.useTimeMinutes ? -1 : 1;
  }
}
