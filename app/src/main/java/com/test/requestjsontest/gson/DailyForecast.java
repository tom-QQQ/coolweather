package com.test.requestjsontest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 瞿康宁 on 2017/2/25.
 */

public class DailyForecast {


    @SerializedName("astro")
    public AstronomyIndex astronomyIndex;

    /**
     * 天文指数
     */
    public class AstronomyIndex{

        @SerializedName("mr")
        public String moonrise;

        @SerializedName("ms")
        public String moonset;

        @SerializedName("sr")
        public String sunrise;

        @SerializedName("ss")
        public String sunset;
    }

    @SerializedName("cond")
    public DayNightCode dayNightCode;

    public class DayNightCode{

        @SerializedName("code_d")
        public String dayCode;

        @SerializedName("code_n")
        public String nightCode;

        @SerializedName("txt_d")
        public String dayInfo;

        @SerializedName("txt_n")
        public String nightInfo;
    }

    public String date;

    /**
     * 相对湿度
     */
    @SerializedName("hum")
    public String relativeHumidity;

    /**
     * 降水量
     */
    @SerializedName("pcpn")
    public String precipitation;

    /**
     * 降水概率
     */
    @SerializedName("pop")
    public String precipitationProbability;

    /**
     * 气压
     */
    @SerializedName("pres")
    public String pressure;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature {

        public String max;
        public String min;
    }

    /**
     * 紫外线指数
     */
    @SerializedName("uv")
    public String ultravioletIndex;

    /**
     * 能见度
     */
    @SerializedName("vis")
    public String visibility;

    public Wind wind;

    public class Wind{

        @SerializedName("deg")
        public String windDegree;

        @SerializedName("dir")
        public String windDirection;

        @SerializedName("sc")
        public String windSpeedGrade;

        @SerializedName("spd")
        public String windSpeed;
    }
}
