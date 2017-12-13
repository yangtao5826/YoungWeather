package com.youngweather.youngweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 17670 on 2017/12/12.
 * 解析json数据中daily_forecast字段
 */

public class Forecast {
    @SerializedName("cond_code_d")
    public String condCodeD;//白天天气状况代码
    @SerializedName("cond_code_n")
    public String condCodeN;//晚间天气状况代码
    @SerializedName("cond_txt_d")
    public String condTextD;//白天天气状况描述
    @SerializedName("cond_txt_n")
    public String condTextN;//晚间天气状况描述
    public String date;//预报日期
    public String hum;//相对湿度
    public String mr;//月升时间
    public String ms;//月落时间
    public String pcpn;//降水量
    public String pop;//降水概率
    public String pres;//大气压强
    public String sr;//日出时间
    public String ss;//日落时间
    @SerializedName("tmp_max")
    public String tmpMax;//最高温度
    @SerializedName("tmp_min")
    public String tmpMin;//最低温度
    @SerializedName("uv_index")
    public String uvIndex;//紫外线强度指数
    public String vis;//能见度，单位：公里
    @SerializedName("wind_deg")
    public String windDeg;//风向360角度
    @SerializedName("wind_dir")
    public String windDir;//风向
    @SerializedName("wind_sc")
    public String windSc;//风力
    @SerializedName("wind_spd")
    public String windSpd;//风速，公里/小时
}
