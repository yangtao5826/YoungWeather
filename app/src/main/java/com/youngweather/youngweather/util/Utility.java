package com.youngweather.youngweather.util;

/**
 * Created by 17670 on 2017/12/11.
 */

import android.text.TextUtils;

import com.google.gson.Gson;
import com.youngweather.youngweather.db.City;
import com.youngweather.youngweather.db.County;
import com.youngweather.youngweather.db.Province;
import com.youngweather.youngweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于解析Json数据
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     * @param response 服务器返回的json数据
     * @return 解析并保存成功 返回true 否则返回false
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0 ; i < allProvinces.length() ; i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//保存到数据库
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级的数据
     * @param response 服务器返回的json数据
     * @param provinceId 该市所处的省的ID
     * @return 处理并保存成功 返回true 否则返回false
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response);
                for (int i = 0 ; i < allCities.length() ; i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级的json数据
     * @param response 服务器返回的json数据
     * @param cityId 该县所在的市的ID
     * @return 处理和保存成功 返回true  否则返回false
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0 ; i < allCounties.length() ; i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
//                    county.setWeatherId(countyObject.getInt("id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
