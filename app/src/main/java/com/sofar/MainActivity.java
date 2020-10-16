package com.sofar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sofar.business.statistics.StatisticsFragment;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_container);

    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.fragment_container, new StatisticsFragment())
      .commitAllowingStateLoss();
  }
}