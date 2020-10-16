package com.sofar.business.statistics.app;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;

public class AppUseAdapter extends RecyclerAdapter<AppUseInfo> {

  @Override
  protected int getItemLayoutId(int viewType) {
    return R.layout.app_use_item;
  }

  @NonNull
  @Override
  protected RecyclerViewBinder onCreateViewBinder(int viewType) {
    return new AppUseItemViewBinder();
  }

}
