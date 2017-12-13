package com.youngweather.youngweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 17670 on 2017/12/12.
 * 解析从服务器返回天气数据主类
 */

public class Weather {
    public Basic basic;
    public Update update;
    public String status;
    public Now now;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyleList;

}
