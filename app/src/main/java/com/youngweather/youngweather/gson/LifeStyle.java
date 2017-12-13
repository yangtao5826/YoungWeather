package com.youngweather.youngweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 17670 on 2017/12/12.
 * 解析lifestyle字段
 */

public class LifeStyle {
    public String brf;//生活指数简介
    public String txt;//生活指数详细描述
    @SerializedName("type")
    public String typeMode;//生活指数类型 comf：舒适度指数、cw：洗车指数、drsg：穿衣指数、flu：感冒指数、sport：运动指数、trav：旅游指数、uv：紫外线指数、air：空气污染扩散条件指数
}
