package com.youngweather.youngweather.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by 17670 on 2017/12/11.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }
    public static Context getContext(){
        return context;
    }
}
