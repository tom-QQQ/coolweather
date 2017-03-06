package com.test.requestjsontest.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.test.requestjsontest.gson.Weather;
import com.test.requestjsontest.util.HttpUtil;
import com.test.requestjsontest.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新数据只需要更新SharedPreferences的数据即可
        updateWeather();
        updateBingPic();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int updateFrequency = prefs.getInt("update_frequency", 8);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int frequency = updateFrequency * 60 * 1000;
        //更新频率为0时，关闭服务
        if (frequency == 0) {
            Intent stopIntent = new Intent(this, AutoUpdateService.class);
            stopService(stopIntent);
        } else {
            long triggerAtTime = SystemClock.elapsedRealtime() + frequency;
            Intent i = new Intent(this, AutoUpdateService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            Log.d("更新时间", "天气更新了,更新频率为：" + updateFrequency + "小时/次");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            final Weather weather = Utility.handleWeatherResponse(weatherString);
            if (weather != null) {

                String weatherId = weather.basic.weatherId;
                String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                        weatherId +"&key=ff19f0ae8681444e8e4ec561b968f9f8";
                HttpUtil.sendHttpRequest(weatherUrl, new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Weather newWeather = Utility.handleWeatherResponse(responseText);
                        if (newWeather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor  editor = PreferenceManager.
                                    getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                        }

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                });
            } else {
                updateWeather();
            }
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                String oldBingPic = prefs.getString("bing_pic", "");
                //返回图片链接与之前相同时，不更新
                if (!bingPic.equals(oldBingPic)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("bing_pic", bingPic);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
