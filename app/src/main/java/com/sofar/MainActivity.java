package com.sofar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sofar.business.statistics.AppUseManager;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    AppUseManager.get().getAppUse();
  }
}