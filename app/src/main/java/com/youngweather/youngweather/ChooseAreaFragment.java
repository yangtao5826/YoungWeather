package com.youngweather.youngweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.youngweather.youngweather.db.City;
import com.youngweather.youngweather.db.County;
import com.youngweather.youngweather.db.Province;
import com.youngweather.youngweather.gson.Weather;
import com.youngweather.youngweather.util.HttpUtil;
import com.youngweather.youngweather.util.MyApplication;
import com.youngweather.youngweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 17670 on 2017/12/11.
 */

public class ChooseAreaFragment extends Fragment{

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private static final String APP_KEY = "b8fe6d05e62cb747";//急速数据查询中国行政规划的APP_KEY
    private static final String TAG = "ChooseAreaFragment";

    private ProgressDialog progressDialog;
    private TextView titleText;//标题栏文字
    private Button backButton;//返回按钮
    private TextView positionText;//定位地址
    private ListView listView;//地址显示列表
    private LocationAdapter adapter;//地址适配器
    private List<String> dataList = new ArrayList<>();//将要传给适配器的地址列表
    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//县列表
    private Province selectedProvince;//当前选中的省份
    private City selectedCity;//当前选中的城市
    private int currentLevel;//当前选中的级别
    private LocationClient mLocationClient;//百度地图定位
    private BDLocation mBdLocation;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.location_list_view);
        positionText = view.findViewById(R.id.position_text);
        adapter = new LocationAdapter(MyApplication.getContext(),R.layout.location_item,dataList);
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        listView.setAdapter(adapter);//设置recyclerView的适配器
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestLocation();//获取当前地址并显示到页面上
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    //跳转到天气显示界面
                    turnToWeather(countyList.get(position).getCountyName());
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
        positionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = mBdLocation.getDistrict();
                turnToWeather(location);
            }
        });
    }

    /**
     * 跳转到天气显示界面
     * @param countyName
     */
    private void turnToWeather(String countyName) {
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        intent.putExtra("location",countyName);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * 查询全国所有的县，优先查询数据库，如果没有查询到就去服务器上去查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int parentid = selectedCity.getCityCode();
            String address = "http://api.jisuapi.com/area/town?parentid="+parentid+"&appkey="+APP_KEY;
            queryFromSever(address,"county");
        }
    }

    /**
     * 查询全国所有的市，优先查询数据库，如果没有查询到就去服务器上去查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int parentid = selectedProvince.getProvinceCode();
            String address = "http://api.jisuapi.com/area/city?parentid="+parentid+"&appkey="+APP_KEY;
            queryFromSever(address,"city");
        }
    }

    /**
     * 查询全国所有的省，优先查询数据库，如果没有查询到就去服务器上去查询
     */
    private void queryProvinces() {
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);//查询数据库中所有的省份
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://api.jisuapi.com/area/province?appkey="+APP_KEY;
            queryFromSever(address,"province");
        }
    }

    /**
     * 根据传入的地址和类型从服务器查询所有省县市的数据
     * @param address 查询地址
     * @param type 类型
     */
    private void queryFromSever(String address,final String type){
        showProgressDialog();//显示查询进度条
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MyApplication.getContext(),"加载失败，请稍后重试！",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                int start = responseText.indexOf("[");
                int end = responseText.lastIndexOf("]");
                if ((start==-1)||(end==-1)){
                    //当前情况为直辖市  下级只有区级（对应正常的市级）
                    dataList.clear();
                    dataList.add(selectedCity.getCityName());
                    if (countyList!=null||countyList.size()>0){
                        countyList.clear();
                        County county = new County();
                        county.setCountyName(selectedCity.getCityName());
                        county.setCityId(selectedCity.getProvinceId());
                        countyList.add(county);
                    }
                    currentLevel = LEVEL_COUNTY;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setSelection(0);
                            adapter.notifyDataSetChanged();
                            closeProgressDialog();
                        }
                    });
                    return;
                }
                responseText = responseText.substring(start,end+1);
                Log.d(TAG, "onResponse: "+responseText);
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();//关闭查询进度条
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

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
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载，请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 获取当前地址
     */
    private void requestLocation() {
        initLocation();//设置获取方式
        mLocationClient.start();//开始获取地址
    }

    /**
     * 设置获取当前地址的方式和相关设置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);//需要获取当前详细的地址信息
        mLocationClient.setLocOption(option);
    }

    /**
     * 获取地址成功调用的函数
     */
    class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mBdLocation = bdLocation;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BDLocation location = getBdlocation();
                    titleText.setText(location.getCountry());//显示当前国家
                    String district = location.getProvince()+"-"+location.getCity()+"-"+location.getDistrict();
                    positionText.setText(district);//显示当前地址
                }
            });
        }
    }

    private BDLocation getBdlocation() {
        return mBdLocation;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
