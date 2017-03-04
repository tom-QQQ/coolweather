package com.test.requestjsontest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 瞿康宁 on 2017/2/25.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("cnty")
    public String country;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("lat")
    public String latitude;

    @SerializedName("lon")
    public String longitude;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

        public String utc;
    }
}
