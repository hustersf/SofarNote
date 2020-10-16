package com.sofar.business.statistics.app;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;
import com.sofar.base.recycler.itemdecoration.LinearMarginItemDecoration;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.utility.CollectionUtil;
import com.sofar.utility.DeviceUtil;

public class AppUsePageItemViewBinder extends RecyclerViewBinder<AppUsePageInfo> {

  TextView timeTv;

  RecyclerView recycler;
  AppUseAdapter adapter;

  @Override
  protected void onCreate() {
    super.onCreate();
    timeTv = view.findViewById(R.id.time);
    recycler = view.findViewById(R.id.app_use_recycler);
    adapter = new AppUseAdapter();
    recycler.addItemDecoration(new LinearMarginItemDecoration(RecyclerView.VERTICAL, DeviceUtil.dp2px(context, 10)));
    recycler.setAdapter(adapter);
    recycler.setLayoutManager(new LinearLayoutManager(context));
  }

  @Override
  protected void onBind(AppUsePageInfo data) {
    super.onBind(data);
    long minute = 0;
    if (!CollectionUtil.isEmpty(data.list)) {
      for (AppUseInfo info : data.list) {
        minute += info.useTimeMinutes;
      }

      for (AppUseInfo info : data.list) {
        info.timePercent = (int) (info.useTimeMinutes * 100 / minute);
      }
      adapter.setList(data.list);
      adapter.notifyDataSetChanged();
    }

    int h = (int) (minute / 60);
    int min = (int) (minute % 60);
    timeTv.setText(data.day + "  App前台运行总时长 " + format(h, min));
  }

  private String format(int h, int min) {
    StringBuffer sb = new StringBuffer();
    if (h > 0) {
      sb.append(h);
      sb.append("小时");
    }
    sb.append(min);
    sb.append("分钟");
    return sb.toString();
  }
}
