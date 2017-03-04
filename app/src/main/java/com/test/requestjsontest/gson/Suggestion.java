package com.test.requestjsontest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 瞿康宁 on 2017/2/26.
 */

public class Suggestion {

    @SerializedName("air")
    public AirPollutionMeteorological airPollutionMeteorological;

    public class AirPollutionMeteorological {

        @SerializedName("brf")
        public String airPollutionMeteorologicalBrief;

        @SerializedName("txt")
        public String airPollutionMeteorologicalInfo;
    }


    @SerializedName("comf")
    public Comfort comfort;

    public class Comfort {

        @SerializedName("brf")
        public String comfortBrief;

        @SerializedName("txt")
        public String comfortInfo;
    }


    @SerializedName("cw")
    public CarWashing carWashing;

    public class CarWashing {

        @SerializedName("brf")
        public String carWashingBrief;

        @SerializedName("txt")
        public String carWashingInfo;
    }


    @SerializedName("drsg")
    public Dressing dressing;

    public class Dressing {

        @SerializedName("brf")
        public String dressingBrief;

        @SerializedName("txt")
        public String dressingInfo;
    }


    @SerializedName("flu")
    public Influenza influenza;

    /**
     * 流行性感冒
     */
    public class Influenza {

        @SerializedName("brf")
        public String influenzaBrief;

        @SerializedName("txt")
        public String influenzaInfo;
    }


    public Sport sport;

    public class Sport {

        @SerializedName("brf")
        public String sportingBrief;

        @SerializedName("txt")
        public String sportingInfo;
    }


    @SerializedName("trav")
    public Traveling traveling;

    public class Traveling {

        @SerializedName("brf")
        public String travelingBrief;

        @SerializedName("txt")
        public String travelingInfo;
    }


    @SerializedName("uv")
    public Ultraviolet ultraviolet;

    public class Ultraviolet {

        @SerializedName("brf")
        public String ultravioletBrief;

        @SerializedName("txt")
        public String ultravioletInfo;
    }
}
