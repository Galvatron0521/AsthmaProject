package com.shenkangyun.asthmaproject.Activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.shenkangyun.asthmaproject.BaseFolder.Const;
import com.shenkangyun.asthmaproject.BeanFolder.AirEntity;
import com.shenkangyun.asthmaproject.BeanFolder.ProvinceEntity;
import com.shenkangyun.asthmaproject.BeanFolder.WeatherEntity;
import com.shenkangyun.asthmaproject.DBFolder.AirDB;
import com.shenkangyun.asthmaproject.DBFolder.CityDB;
import com.shenkangyun.asthmaproject.DBFolder.CountryDB;
import com.shenkangyun.asthmaproject.DBFolder.ProvinceDB;
import com.shenkangyun.asthmaproject.DBFolder.WeatherDB;
import com.shenkangyun.asthmaproject.R;
import com.shenkangyun.asthmaproject.Utils.LocationService;
import com.shenkangyun.asthmaproject.WeatherFragment.ProvinceFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class WeatherActivity extends AppCompatActivity implements DrawerLayout.DrawerListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int LOCATION_CODE = 100;
    @BindView(R.id.toolBar_title)
    TextView toolBarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.frame_change)
    FrameLayout frameChange;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tv_regionName)
    TextView tvRegionName;
    @BindView(R.id.tv_wendu)
    TextView tvWendu;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_yubao)
    TextView tvYubao;
    @BindView(R.id.tv_one_one)
    TextView tvOneOne;
    @BindView(R.id.tv_one_two)
    TextView tvOneTwo;
    @BindView(R.id.tv_one_three)
    TextView tvOneThree;
    @BindView(R.id.tv_one_four)
    TextView tvOneFour;
    @BindView(R.id.tv_two_one)
    TextView tvTwoOne;
    @BindView(R.id.tv_two_two)
    TextView tvTwoTwo;
    @BindView(R.id.tv_two_three)
    TextView tvTwoThree;
    @BindView(R.id.tv_two_four)
    TextView tvTwoFour;
    @BindView(R.id.tv_three_one)
    TextView tvThreeOne;
    @BindView(R.id.tv_three_two)
    TextView tvThreeTwo;
    @BindView(R.id.tv_three_three)
    TextView tvThreeThree;
    @BindView(R.id.tv_three_four)
    TextView tvThreeFour;
    @BindView(R.id.card_one)
    CardView cardOne;
    @BindView(R.id.air)
    TextView air;
    @BindView(R.id.comf)
    TextView comf;
    @BindView(R.id.cw)
    TextView cw;
    @BindView(R.id.drsg)
    TextView drsg;
    @BindView(R.id.flu)
    TextView flu;
    @BindView(R.id.sport)
    TextView sport;
    @BindView(R.id.trav)
    TextView trav;
    @BindView(R.id.uv)
    TextView uv;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_aqi)
    TextView tvAqi;
    @BindView(R.id.tv_qlty)
    TextView tvQlty;
    @BindView(R.id.tv_main)
    TextView tvMain;
    @BindView(R.id.tv_co)
    TextView tvCo;
    @BindView(R.id.tv_pm10)
    TextView tvPm10;
    @BindView(R.id.tv_pm25)
    TextView tvPm25;
    @BindView(R.id.tv_no2)
    TextView tvNo2;
    @BindView(R.id.tv_so2)
    TextView tvSo2;

    private LocationService locService;
    private FragmentManager fragmentManager;
    private boolean isOk = true;
    private SharedPreferences sharedPreferences;

    private String province;
    private String city;
    private String district;

    private String subLast;
    private String subFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.toolBar));
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            toolBarTitle.setText("天气");
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        initView();
        showCheckPermissions();
        initSp();
        initImageBg();
        initDB();
    }

    private void initImageBg() {
        SharedPreferences sp = getSharedPreferences("Image", MODE_PRIVATE);
        String url = sp.getString("url", null);
        if (url != null) {
            Glide.with(WeatherActivity.this).load(url).into(imgBack);
        }
        OkHttpUtils.get()
                .url(Const.URL_IMAGE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SharedPreferences sp = getSharedPreferences("Image", MODE_PRIVATE);
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("url", response);
                        ed.commit();
                        Glide.with(WeatherActivity.this).load(response).into(imgBack);
                    }
                });
    }

    private void initDB() {
        List<AirDB> airDBS = LitePal.findAll(AirDB.class);
        List<WeatherDB> all = LitePal.findAll(WeatherDB.class);
        if (!all.isEmpty()) {
            tvRegionName.setText(all.get(0).getTitle());
            tvWendu.setText(all.get(0).getWendu());
            tvState.setText(all.get(0).getState());
            tvOneOne.setText(all.get(0).getOneOne());
            tvOneTwo.setText(all.get(0).getOneTwo());
            tvOneThree.setText(all.get(0).getOneThree());
            tvOneFour.setText(all.get(0).getOneFour());

            tvTwoOne.setText(all.get(0).getTwoOne());
            tvTwoTwo.setText(all.get(0).getTwoTwo());
            tvTwoThree.setText(all.get(0).getTwoThree());
            tvTwoFour.setText(all.get(0).getTwoFour());

            tvThreeOne.setText(all.get(0).getThreeOne());
            tvThreeTwo.setText(all.get(0).getThreeTwo());
            tvThreeThree.setText(all.get(0).getThreeThree());
            tvThreeFour.setText(all.get(0).getThreeFour());

            air.setText("空气指数：" + all.get(0).getAir());
            comf.setText("舒适度指数：" + all.get(0).getComf());
            cw.setText("洗车指数：" + all.get(0).getCw());
            drsg.setText("穿衣指数：" + all.get(0).getDrsg());
            flu.setText("感冒指数：" + all.get(0).getFlu());
            sport.setText("运动指数：" + all.get(0).getSport());
            trav.setText("旅游指数：" + all.get(0).getTrav());
            uv.setText("紫外线指数：" + all.get(0).getUv());
        }

        if (!airDBS.isEmpty()) {
            tvAqi.setText(airDBS.get(0).getAqi());
            tvMain.setText(airDBS.get(0).getMain());
            tvQlty.setText(airDBS.get(0).getQlty());
            tvCo.setText(airDBS.get(0).getCo());
            tvPm10.setText(airDBS.get(0).getPm10());
            tvPm25.setText(airDBS.get(0).getPm25());
            tvSo2.setText(airDBS.get(0).getSo2());
            tvNo2.setText(airDBS.get(0).getNo2());
        }
    }


    private void initView() {
        fragmentManager = getSupportFragmentManager();
        locService = new LocationService(this);
        locService.registerListener(listener);
        locService.start();
        drawerLayout.addDrawerListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    private boolean showCheckPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOCATION_CODE);
            }
            return true;
        } else {
            getAddressInfo();
            return false;
        }
    }

    private void getAddressInfo() {
        if (locService != null) {
            locService.start();
        }
    }

    private void initSp() {
        sharedPreferences = getSharedPreferences("Cool", MODE_PRIVATE);
        boolean spIsOk = sharedPreferences.getBoolean("spIsOk", false);
        if (!spIsOk) {
            initHttp();
        }
    }

    private void initHttp() {
        OkHttpUtils
                .get()
                .url(Const.URL_PROVINCE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        List<ProvinceEntity> ProvinceList = gson.fromJson(response, new TypeToken<List<ProvinceEntity>>() {
                        }.getType());

                        for (ProvinceEntity entity : ProvinceList) {
                            ProvinceDB provinceDB = new ProvinceDB();
                            provinceDB.setProvinceID(entity.getId());
                            provinceDB.setProvinceName(entity.getName());
                            provinceDB.save();
                        }
                        sharedPreferences = getSharedPreferences("Cool", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("spIsOk", true);
                        editor.commit();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    getAddressInfo();
                }
            }
        }
    }

    /***
     * 定位结果回调，在此方法中处理定位结果
     */
    private BDAbstractLocationListener listener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            province = location.getProvince();    //获取省份
            city = location.getCity();    //获取城市
            district = location.getDistrict();    //获取区县

            int i = district.length();
            subLast = district.substring(district.length() - 1, i);
            if ("区".equals(subLast)) {
                subFront = district.substring(0, district.length() - 1);
                tvRegionName.setText(subFront);
            } else {
                subFront = district;
                tvRegionName.setText(subFront);
            }

            getWeatherInfo(subFront);
            getAirInfo(city);
        }

    };

    private void getAirInfo(String city) {
        String cityName = null;
        if (TextUtils.isEmpty(city)) {
            cityName = "北京市";
        } else {
            cityName = city;
        }

        CityDB cityDB = new CityDB();
        cityDB.setName(cityName);
        cityDB.save();

        OkHttpUtils
                .get()
                .url(Const.URL_AIR + cityName)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        AirEntity airEntity = gson.fromJson(response, AirEntity.class);
                        getAir(airEntity);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getWeatherInfo(String district) {
        String weather_id = null;
        if (TextUtils.isEmpty(district)) {
            weather_id = "北京";
        } else {
            weather_id = district;
        }
        CountryDB countryDB = new CountryDB();
        countryDB.setName(weather_id);
        countryDB.save();
        OkHttpUtils
                .get()
                .url(Const.URL_WEATHER + weather_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        WeatherEntity weatherEntity = gson.fromJson(response, WeatherEntity.class);
                        getWeather(weatherEntity);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locService.unregisterListener(listener);
        locService.stop();
    }

    @OnClick({R.id.img_position, R.id.img_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_position:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.img_cancel:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDrawerStateChanged(int i) {
        if (i > 0 && isOk == true) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_change, new ProvinceFragment());
            transaction.commit();
            isOk = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {

    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_change, new ProvinceFragment());
        transaction.commit();
        isOk = true;
    }

    @Override
    public void onRefresh() {
        if (tvRegionName.getText().toString().contains("—")) {
            Toast.makeText(this, "请选择一个城市", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            List<CountryDB> countryDBs = LitePal.where("name = ?", tvRegionName.getText().toString()).find(CountryDB.class);
            String weather_id = countryDBs.get(0).getName();
            OkHttpUtils
                    .get()
                    .url(Const.URL_WEATHER + weather_id)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Gson gson = new Gson();
                            WeatherEntity weatherEntity = gson.fromJson(response, WeatherEntity.class);
                            getWeather(weatherEntity);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    public void getWeather(WeatherEntity weatherEntity) {
        try {
            LitePal.deleteAll(WeatherDB.class);
            WeatherDB weatherDB = new WeatherDB();

            tvRegionName.setText(weatherEntity.getHeWeather6().get(0).getBasic().getLocation());
            tvWendu.setText(weatherEntity.getHeWeather6().get(0).getNow().getTmp() + "℃");
            tvState.setText(weatherEntity.getHeWeather6().get(0).getNow().getCond_txt());

            tvOneOne.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(0).getDate());
            tvOneTwo.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(0).getCond_txt_d() + "/" + weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(0).getCond_txt_n());
            tvOneThree.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_max());
            tvOneFour.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_min());

            tvTwoOne.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(1).getDate());
            tvTwoTwo.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(1).getCond_txt_d() + "/" + weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(1).getCond_txt_n());
            tvTwoThree.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(1).getTmp_max());
            tvTwoFour.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(1).getTmp_min());

            tvThreeOne.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(2).getDate());
            tvThreeTwo.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(2).getCond_txt_d() + "/" + weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(2).getCond_txt_n());
            tvThreeThree.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(2).getTmp_max());
            tvThreeFour.setText(weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(2).getTmp_min());

            comf.setText("舒适度指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(0).getTxt());
            drsg.setText("穿衣指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(1).getTxt());
            flu.setText("感冒指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(2).getTxt());
            sport.setText("运动指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(3).getTxt());
            trav.setText("旅游指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(4).getTxt());
            uv.setText("紫外线指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(5).getTxt());
            cw.setText("洗车指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(6).getTxt());
            air.setText("空气指数：" + weatherEntity.getHeWeather6().get(0).getLifestyle().get(7).getTxt());

            weatherDB.setTitle(tvRegionName.getText().toString());
            weatherDB.setWendu(tvWendu.getText().toString());
            weatherDB.setState(tvState.getText().toString());
            weatherDB.setOneOne(tvOneOne.getText().toString());
            weatherDB.setOneTwo(tvOneTwo.getText().toString());
            weatherDB.setOneThree(tvOneThree.getText().toString());
            weatherDB.setOneFour(tvOneFour.getText().toString());
            weatherDB.setTwoOne(tvTwoOne.getText().toString());
            weatherDB.setTwoTwo(tvTwoTwo.getText().toString());
            weatherDB.setTwoThree(tvTwoThree.getText().toString());
            weatherDB.setTwoFour(tvTwoFour.getText().toString());
            weatherDB.setThreeOne(tvThreeOne.getText().toString());
            weatherDB.setThreeTwo(tvThreeTwo.getText().toString());
            weatherDB.setThreeThree(tvThreeThree.getText().toString());
            weatherDB.setThreeFour(tvThreeFour.getText().toString());
            weatherDB.setAir(air.getText().toString());
            weatherDB.setComf(comf.getText().toString());
            weatherDB.setCw(cw.getText().toString());
            weatherDB.setDrsg(drsg.getText().toString());
            weatherDB.setFlu(flu.getText().toString());
            weatherDB.setSport(sport.getText().toString());
            weatherDB.setTrav(trav.getText().toString());
            weatherDB.setUv(uv.getText().toString());
            weatherDB.save();
        } catch (Exception e) {
            e.printStackTrace();
            tvRegionName.setText("— —");
            tvWendu.setText("——");
            tvState.setText("——");
        }
    }

    public void getAir(AirEntity airEntity) {
        try {
            LitePal.deleteAll(AirDB.class);
            AirDB airDB = new AirDB();

            tvAqi.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getAqi());
            tvMain.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getMain());
            tvQlty.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getQlty());
            tvCo.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getCo());
            tvPm10.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getPm10());
            tvPm25.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getPm25());
            tvSo2.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getSo2());
            tvNo2.setText(airEntity.getHeWeather6().get(0).getAir_now_city().getNo2());

            airDB.setAqi(airEntity.getHeWeather6().get(0).getAir_now_city().getAqi());
            airDB.setMain(airEntity.getHeWeather6().get(0).getAir_now_city().getMain());
            airDB.setQlty(airEntity.getHeWeather6().get(0).getAir_now_city().getQlty());
            airDB.setCo(airEntity.getHeWeather6().get(0).getAir_now_city().getCo());
            airDB.setPm10(airEntity.getHeWeather6().get(0).getAir_now_city().getPm10());
            airDB.setPm25(airEntity.getHeWeather6().get(0).getAir_now_city().getPm25());
            airDB.setSo2(airEntity.getHeWeather6().get(0).getAir_now_city().getSo2());
            airDB.setNo2(airEntity.getHeWeather6().get(0).getAir_now_city().getNo2());
            airDB.save();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void close() {
        drawerLayout.closeDrawers();
    }
}
