package com.test.requestjsontest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 瞿康宁 on 2017/2/26.
 */

public class Now {

    @SerializedName("cond")
    public NowDayNightCode nowDayNightCode;

    public class NowDayNightCode{

        @SerializedName("code")
        public String nowCode;

        @SerializedName("txt")
        public String nowInfo;
    }

    @SerializedName("fl")
    public String feelTemperature;

    @SerializedName("hum")
    public String nowRelativeHumidity;

    @SerializedName("pcpn")
    public String nowPrecipitation;

    @SerializedName("pres")
    public String nowPressure;

    @SerializedName("tmp")
    public String nowTemperature;

    @SerializedName("vis")
    public String nowVisibility;

    @SerializedName("wind")
    public NowWind nowWind;

    public class NowWind{

        @SerializedName("deg")
        public String nowWindDegree;

        @SerializedName("dir")
        public String nowWindDirection;

        @SerializedName("sc")
        public String nowWindSpeedGrade;

        @SerializedName("spd")
        public String nowWindSpeed;
    }
}
