package com.test.requestjsontest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 瞿康宁 on 2017/2/25.
 */

public class AQI {

    public AQICity city;

    public class AQICity {

        public String aqi;
        public String co;
        public String no2;
        public String o3;
        public String pm10;
        public String pm25;

        @SerializedName("qlty")
        public String quality;

        public String so2;
    }
}
