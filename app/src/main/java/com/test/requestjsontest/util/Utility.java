package com.test.requestjsontest.util;

import com.google.gson.Gson;
import com.test.requestjsontest.gson.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by 瞿康宁 on 2017/2/27.
 */

public class Utility {

    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据风力等级字符串判断对应的名称
     * @param value 风力等级字符串
     * @return 风力名称，X风到X风
     */
    public static String windDegree(String value) {

        byte[] b = value.getBytes();
        if (value.contains("风") ) {
            return value;
        } else {
            return  getDegreeName(b[0]) + "到" + getDegreeName(b[2]);
        }
    }

    /**
     * 根据风力等级判断对应的名称
     * @param degree 风力等级
     * @return 风力名称
     */
    private static String getDegreeName(int degree) {

        switch (degree-48) {
            case 0:
                return "无风";
            case 1:
                return "软风";
            case 2:
                return "轻风";
            case 3:
                return "微风";
            case 4:
                return "和风";
            case 5:
                return "清风";
            case 6:
                return "强风";
            case 7:
                return "劲风";
            case 8:
                return "大风";
            case 9:
                return "烈风";
            case 10:
                return "狂风";
            case 11:
                return "暴风";
            default:
                return "台风";
        }
    }
}
