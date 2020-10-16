package com.sofar.business.statistics.app;

import android.app.usage.UsageStats;
import android.text.format.DateUtils;

import androidx.viewpager2.widget.ViewPager2;

import com.sofar.R;
import com.sofar.base.viewbinder.ViewBinder;
import com.sofar.utility.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AppUsePageViewBinder extends ViewBinder {

  ViewPager2 pager2;
  AppUsePageAdapter adapter;

  @Override
  protected void onCreate() {
    super.onCreate();
    pager2 = view.findViewById(R.id.app_use_pager);
    adapter = new AppUsePageAdapter();
    pager2.setAdapter(adapter);
  }

  @Override
  protected void onBind(Object data) {
    super.onBind(data);

    AppUseManager.get().queryAppUseRecent(7).subscribe(map -> {

      if (CollectionUtil.isEmpty(map)) {
        AppUseManager.get().startSettingPage();
        return;
      }

      List<AppUsePageInfo> dayUseList = new ArrayList<>();
      for (Map.Entry<String, List<UsageStats>> entry : map.entrySet()) {
        String key = entry.getKey();
        List<UsageStats> value = entry.getValue();
        AppUsePageInfo item = new AppUsePageInfo();
        item.day = key;
        item.list = convert(value);
        dayUseList.add(item);
      }
      adapter.setList(dayUseList);
      pager2.setCurrentItem(dayUseList.size() - 1);
      adapter.notifyDataSetChanged();
    });
  }


  private List<AppUseInfo> convert(List<UsageStats> stats) {
    List<AppUseInfo> list = new ArrayList<>();
    for (UsageStats item : stats) {
      AppUseInfo info = new AppUseInfo();
      info.appName = AppUtil.getAppName(item.getPackageName());
      info.appIcon = AppUtil.getAppIcon(item.getPackageName());
      info.useTimeMinutes = item.getTotalTimeInForeground() / DateUtils.MINUTE_IN_MILLIS;
      list.add(info);
    }
    Collections.sort(list);
    return list;
  }

}
