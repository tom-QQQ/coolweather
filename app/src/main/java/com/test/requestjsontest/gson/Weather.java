package com.test.requestjsontest.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 瞿康宁 on 2017/2/26.
 */

public class Weather {

    public AQI aqi;
    public Basic basic;

    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;

    @SerializedName("hourly_forecast")
    public List<HourlyForecast> hourlyForecastList;

    public Now now;
    public String status;
    public Suggestion suggestion;
}
