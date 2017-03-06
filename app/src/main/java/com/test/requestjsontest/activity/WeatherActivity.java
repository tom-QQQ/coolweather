package com.test.requestjsontest.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.requestjsontest.R;
import com.test.requestjsontest.gson.DailyForecast;
import com.test.requestjsontest.gson.HourlyForecast;
import com.test.requestjsontest.gson.Weather;
import com.test.requestjsontest.service.AutoUpdateService;
import com.test.requestjsontest.util.HttpUtil;
import com.test.requestjsontest.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public Button navButton;

    private ScrollView weatherLayout;

    //title
    private TextView titleCity;
    private TextView titleUpdateTime;

    //now
    private TextView nowWeatherInfoText;
    private TextView nowWindGradeText;
    private TextView nowRelativeHumidityText;
    private TextView nowVisibilityText;
    private TextView nowTemperatureText;

    private LinearLayout dailyForecastLayout;
    private LinearLayout hourlyForecastLayout;

    //aqi
    private TextView aqiText;
    private TextView aqiQualityText;
    private TextView pm25Text;

    private LinearLayout suggestionLayout;

    //背景图片
    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

//    private static int state = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //5.0及以上设置状态栏透明
        if (Build.VERSION.SDK_INT > 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);

        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);

        nowWeatherInfoText = (TextView) findViewById(R.id.now_weather_info_text);
        nowWindGradeText = (TextView) findViewById(R.id.now_wind_grade_text);
        nowRelativeHumidityText = (TextView) findViewById(R.id.now_relative_humidity_text);
        nowVisibilityText = (TextView) findViewById(R.id.now_visibility_text);
        nowTemperatureText = (TextView) findViewById(R.id.now_temperature_text);

        dailyForecastLayout = (LinearLayout) findViewById(R.id.daily_forecast_layout);
        hourlyForecastLayout = (LinearLayout) findViewById(R.id.hourly_forecast_layout);

        aqiText = (TextView) findViewById(R.id.aqi_text);
        aqiQualityText = (TextView) findViewById(R.id.air_quality_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);

        suggestionLayout = (LinearLayout) findViewById(R.id.suggestion_layout);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs = getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String bingPic = prefs.getString("bing_pic", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            if (weather != null) {
                mWeatherId = weather.basic.weatherId;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("weather_id", mWeatherId);
                editor.putString("update_time", weather.basic.update.updateTime);
                editor.apply();
                showWeatherInfo(weather);
            } else {
                String weatherId = prefs.getString("weather_id", null);
                if (weatherId != null) {
                    requestWeather(weatherId);
                }
            }
        } else {
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            //加载数据时隐藏天气布局
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        //导航栏
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * 从服务器上查询天气信息，并存储到SharedPreferences中
     * @param weatherId 天气代码
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                weatherId +"&key=ff19f0ae8681444e8e4ec561b968f9f8";
        HttpUtil.sendHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null) {
                            SharedPreferences prefs =
                                    getDefaultSharedPreferences(WeatherActivity.this);
                            String updateTime = prefs.getString("update_time", "");

                            //只有天气更新时间发生变化时，才将服务器返回的数据写入SharedPreferences，并更新时间
                            if (!(updateTime.equals(weather.basic.update.updateTime))) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("weather", responseText);
                                editor.putString("update_time", weather.basic.update.updateTime+"");
                                editor.apply();
                                showWeatherInfo(weather);
                                Toast.makeText(WeatherActivity.this, "天气更新成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WeatherActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "天气更新失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    /**
     * 显示天气信息
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String nowInfo = weather.now.nowDayNightCode.nowInfo;
            String nowWindGrade = Utility.windDegree(weather.now.nowWind.nowWindSpeedGrade);
            String nowRelativeHumidity = "湿度" + weather.now.nowRelativeHumidity + "%";
            String nowVisibility = "能见度" + weather.now.nowVisibility + "千米";
            String nowTemperature = weather.now.nowTemperature + "℃";
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            nowWeatherInfoText.setText(nowInfo);
            nowWindGradeText.setText(nowWindGrade);
            nowRelativeHumidityText.setText(nowRelativeHumidity);
            nowVisibilityText.setText(nowVisibility);
            nowTemperatureText.setText(nowTemperature);

            addHourlyForecast(weather);
            addDailyForecast(weather);

            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                aqiQualityText.setText(weather.aqi.city.quality);
                pm25Text.setText(weather.aqi.city.pm25);
            }

            addSuggestion(weather);

            //数据加载完毕之后显示天气布局
            weatherLayout.setVisibility(View.VISIBLE);

            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "天气更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加未来几小时天气情况
     */
    private void addHourlyForecast(Weather weather) {
        //清空所有view
        hourlyForecastLayout.removeAllViews();
        for (HourlyForecast forecast : weather.hourlyForecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.hourly_item, hourlyForecastLayout,false);
            TextView timeText = (TextView) view.findViewById(R.id.time_text);
            TextView weatherText = (TextView) view.findViewById(R.id.weather_text);
            TextView temperatureText = (TextView) view.findViewById(R.id.temperature_text);
            TextView windText = (TextView) view.findViewById(R.id.wind_text);
            TextView percentText = (TextView) view.findViewById(R.id.percent_text);

            timeText.setText(forecast.hourTime.split(" ")[1]);
            weatherText.setText(forecast.hourlyCode.hourlyInfo);
            String temperature = forecast.hourlyTemperature + "℃";
            temperatureText.setText(temperature);
            windText.setText(Utility.windDegree(forecast.hourlyWind.hourlyWindSpeedGrade));
            String humidity = forecast.hourlyRelativeHumidity + "%";
            percentText.setText(humidity);
            hourlyForecastLayout.addView(view);
        }
    }

    /**
     * 添加未来几天天气情况
     */
    private void addDailyForecast(Weather weather) {
        //清空所有view
        dailyForecastLayout.removeAllViews();
        for (DailyForecast forecast: weather.dailyForecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, dailyForecastLayout,false);
            TextView timeText = (TextView) view.findViewById(R.id.time_text);
            TextView weatherText = (TextView) view.findViewById(R.id.weather_text);
            TextView temperatureText = (TextView) view.findViewById(R.id.temperature_text);
            TextView windText = (TextView) view.findViewById(R.id.wind_text);
            TextView percentText = (TextView) view.findViewById(R.id.percent_text);

            String time = forecast.date.substring(5);
            timeText.setText(time);
            weatherText.setText(forecast.dayNightCode.dayInfo);
            String temperature = forecast.temperature.min + "℃~" + forecast.temperature.max +"℃";
            temperatureText.setText(temperature);
            windText.setText(forecast.wind.windSpeedGrade);
            String probability = forecast.precipitationProbability + "%";
            percentText.setText(probability);
            dailyForecastLayout.addView(view);
        }
    }

    /**
     * 添加生活建议
     */
    private void addSuggestion(Weather weather) {
        suggestionLayout.removeAllViews();

        add(R.string.dressing_index ,weather.suggestion.dressing.dressingBrief,
                weather.suggestion.dressing.dressingInfo);
        add(R.string.comfort_index, weather.suggestion.comfort.comfortBrief,
                weather.suggestion.comfort.comfortInfo);
        add(R.string.influenza_index, weather.suggestion.influenza.influenzaBrief,
                weather.suggestion.influenza.influenzaInfo);
        add(R.string.traveling_index, weather.suggestion.traveling.travelingBrief,
                weather.suggestion.traveling.travelingInfo);
        add(R.string.air_pollution_index, weather.suggestion.airPollutionMeteorological.airPollutionMeteorologicalBrief,
                weather.suggestion.airPollutionMeteorological.airPollutionMeteorologicalInfo);
        add(R.string.ultraviolet_index, weather.suggestion.ultraviolet.ultravioletBrief,
                weather.suggestion.ultraviolet.ultravioletInfo);
        add(R.string.car_washing_index, weather.suggestion.carWashing.carWashingBrief,
                weather.suggestion.carWashing.carWashingInfo);
        add(R.string.sporting_index, weather.suggestion.sport.sportingBrief,
                weather.suggestion.sport.sportingInfo);
    }

    /**
     * 添加生活建议的方法
     */
    private void add(int id, String brief, String info) {
        View view = LayoutInflater.from(this).inflate(R.layout.suggestion_item, suggestionLayout,false);
        TextView index_name_text = (TextView) view.findViewById(R.id.index_name_text);
        TextView index_text = (TextView) view.findViewById(R.id.index_text);
        TextView index_info_text = (TextView) view.findViewById(R.id.index_info_text);

        index_name_text.setText(id);
        index_text.setText(brief);
        index_info_text.setText(info);
        suggestionLayout.addView(view);
    }

    /**
     * 从网络加载Bing图片
     */
    private void loadBingPic() {
        String requestPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(requestPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String bingPic = response.body().string();
                SharedPreferences.Editor editor =
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //直接用图片链接来加载图片
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.setting:
                settingUpdateFrequency();
                break;
            case R.id.set_out:
                Intent intent = new Intent(this, AutoUpdateService.class);
                stopService(intent);
                finish();
            default:
                break;
        }
        return true;
    }

    private void settingUpdateFrequency() {
        final SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(WeatherActivity.this).edit();

        final String[]  frequency = {"每1小时", "每2小时", "每4小时", "每8小时", "不自动更新"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("请设置自动更新频率");
        dialog.setIcon(R.drawable.icon);
        dialog.setCancelable(false);
        dialog.setSingleChoiceItems(frequency, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        editor.putInt("update_frequency", 1);
                        break;

                    case 1:
                        editor.putInt("update_frequency", 2);
                        break;

                    case 2:
                        editor.putInt("update_frequency", 4);
                        break;

                    case 3:
                        editor.putInt("update_frequency", 8);
                        break;

                    case 4:
                        editor.putInt("update_frequency", 0);
                        break;

                    default:
                        break;
                }
            }
        });

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.apply();
                dialog.dismiss();
                Toast.makeText(WeatherActivity.this, "更新频率设置成功", Toast.LENGTH_SHORT).show();
                //设置完毕更新频率后直接开启服务以刷新更新频率
                Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                startService(intent);
            }
        });
        dialog.create().show();
    }

}
