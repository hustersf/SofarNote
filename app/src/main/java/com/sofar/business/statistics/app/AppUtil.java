package com.sofar.business.statistics.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.sofar.SofarApp;

public class AppUtil {

  /**
   * 获取应用程序图标
   */
  @Nullable
  public static Drawable getAppIcon(String packageName) {
    try {
      // 包管理操作管理类
      Context context = SofarApp.getAppContext();
      PackageManager pm = context.getPackageManager();
      // 获取到应用信息
      ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
      return info.loadIcon(pm);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取应用程序名称
   */
  public static String getAppName(String packageName) {
    try {
      Context context = SofarApp.getAppContext();
      PackageManager packageManager = context.getPackageManager();
      ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
      return packageManager.getApplicationLabel(info).toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

}
