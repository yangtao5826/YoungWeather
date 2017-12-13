package com.youngweather.youngweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 17670 on 2017/12/12.
 * 解析json数据basic字段
 */

public class Basic {

    public String cid;//地区或城市ID

    public String location;//地区或城市名称

    @SerializedName("parent_city")
    public String parentCity;//上级城市

    @SerializedName("admin_area")
    public String adminArea;//所属行政区域

    public String cnty;//所属国家名称

    public String lat;//经度

    public String lon;//纬度

    public String tz;//所属时区
}
