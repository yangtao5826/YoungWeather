package com.youngweather.youngweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 17670 on 2017/12/11.
 */

public class HttpUtil {
    /**
     * 封装OKHTTP 用于向服务器发送HTTP请求
     * @param address 服务器地址
     * @param callback 回调函数 用于处理服务器返回的数据
     */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
