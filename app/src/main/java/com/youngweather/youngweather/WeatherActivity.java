package com.youngweather.youngweather;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.youngweather.youngweather.gson.Forecast;
import com.youngweather.youngweather.gson.LifeStyle;
import com.youngweather.youngweather.gson.Weather;
import com.youngweather.youngweather.util.HttpUtil;
import com.youngweather.youngweather.util.Utility;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;
    private ProgressDialog progressDialog;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private TextView drsgText;
    private TextView fluText;
    private TextView travText;
    private TextView uvText;
    private TextView airText;

    private TextView nowOtherFl;
    private TextView nowOtherHum;
    private TextView nowOtherWind;
    private TextView nowOtherWindText;

    private LinearLayout forecastLayout;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        drsgText  = findViewById(R.id.drsg_text);
        fluText = findViewById(R.id.flu_text);
        travText = findViewById(R.id.trav_text);
        uvText = findViewById(R.id.uv_text);
        airText = findViewById(R.id.air_text);
        nowOtherFl = findViewById(R.id.now_other_fl);
        nowOtherHum = findViewById(R.id.now_other_hum);
        nowOtherWind = findViewById(R.id.now_other_wind);
        nowOtherWindText = findViewById(R.id.now_other_wind_text);
        //swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        navButton = (Button) findViewById(R.id.nav_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.cid;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            showProgressDialog();
            mWeatherId = getIntent().getStringExtra("location");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                requestWeather(mWeatherId);
//            }
//        });
//        navButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/s6/weather?key=02123173cd39441796258ccfda1de405&location="+weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.cid;
                            closeProgressDialog();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        //loadBingPic();
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.location;
        String updateTime = weather.update.loc.split(" ")[1];
        String degree = weather.now.tmp + "℃";
        String weatherInfo = weather.now.condText;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.condTextD);
            maxText.setText(forecast.tmpMax);
            minText.setText(forecast.tmpMin);
            forecastLayout.addView(view);
        }
        nowOtherWind.setText(weather.now.windSc);
        nowOtherWindText.setText(weather.now.windDir);
        nowOtherHum.setText(weather.now.hum);
        nowOtherFl.setText(weather.now.fl+"℃");
        for (LifeStyle lifeStyle : weather.lifeStyleList){
            if (lifeStyle.typeMode.equals("air")){
                airText.setText("空气污染扩散条件指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }else if (lifeStyle.typeMode.equals("comf")){
                comfortText.setText("舒适度指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }else if (lifeStyle.typeMode.equals("drsg")){
                drsgText.setText("穿衣指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }else if (lifeStyle.typeMode.equals("flu")){
                fluText.setText("感冒指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }else if (lifeStyle.typeMode.equals("cw")){
                carWashText.setText("洗车指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }else if (lifeStyle.typeMode.equals("sport")){
                sportText.setText("运动指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }else if (lifeStyle.typeMode.equals("uv")){
                uvText.setText("紫外线指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }else if (lifeStyle.typeMode.equals("trav")){
                travText.setText("旅游指数："+lifeStyle.brf+"\n"+lifeStyle.txt);
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);

//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);
    }

    /**
     * 关闭加载进度条
     */
    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /**
     * 显示加载进度条
     */
    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载，请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

}
