package com.sofar.business.statistics.app;

import android.widget.ImageView;
import android.widget.TextView;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.widget.progress.MusicProgress;

public class AppUseItemViewBinder extends RecyclerViewBinder<AppUseInfo> {

  ImageView appIcon;
  TextView appName;
  TextView time;
  MusicProgress progress;

  @Override
  protected void onCreate() {
    super.onCreate();
    appIcon = view.findViewById(R.id.app_icon);
    appName = view.findViewById(R.id.app_name);
    time = view.findViewById(R.id.app_use_time);
    progress = view.findViewById(R.id.app_use_progress);
  }

  @Override
  protected void onBind(AppUseInfo data) {
    super.onBind(data);
    appIcon.setImageDrawable(data.appIcon);
    appName.setText(data.appName);
    time.setText(data.timePercent + "%  " + data.formatTime());
    progress.setProgress(data.timePercent);
  }
}
