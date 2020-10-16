package com.sofar.business.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sofar.R;
import com.sofar.base.viewbinder.ViewBinder;
import com.sofar.business.statistics.app.AppUsePageViewBinder;

public class StatisticsFragment extends Fragment {

  ViewBinder viewBinder = new ViewBinder();

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.statistics_fragment, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewBinder.addViewBinder(new AppUsePageViewBinder());
    viewBinder.create(view);
    viewBinder.bind(new Object());
  }

}
