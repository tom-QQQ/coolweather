package com.test.requestjsontest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.test.requestjsontest.R;

import org.litepal.LitePal;


public class MainActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", true);
        if (isFirst) {
            LitePal.getDatabase();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, FirstActivity.class);
            startActivity(intent);

            //销毁第一次所启动的活动
            finish();
        }

        //当之前有过缓存天气数据时，直接跳到WeatherActivity界面显示天气
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather", null) != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
