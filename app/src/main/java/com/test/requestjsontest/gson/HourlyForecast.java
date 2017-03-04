package com.test.requestjsontest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 瞿康宁 on 2017/2/26.
 */

public class HourlyForecast {

    @SerializedName("cond")
    public HourlyDayNightCode hourlyCode;

    public class HourlyDayNightCode{

        @SerializedName("code")
        public String hourlyCode;

        @SerializedName("txt")
        public String hourlyInfo;
    }

    @SerializedName("date")
    public String hourTime;

    @SerializedName("hum")
    public String hourlyRelativeHumidity;

    @SerializedName("pop")
    public String hourlyPrecipitationProbability;

    @SerializedName("pres")
    public String hourlyPressure;

    @SerializedName("tmp")
    public String hourlyTemperature;

    @SerializedName("wind")
    public HourlyWind hourlyWind;

    public class HourlyWind{

        @SerializedName("deg")
        public String hourlyWindDegree;

        @SerializedName("dir")
        public String hourlyWindDirection;

        @SerializedName("sc")
        public String hourlyWindSpeedGrade;

        @SerializedName("spd")
        public String hourlyWindSpeed;
    }
}
