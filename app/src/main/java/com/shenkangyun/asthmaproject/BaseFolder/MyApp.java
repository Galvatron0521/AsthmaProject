package com.shenkangyun.asthmaproject.BaseFolder;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.shenkangyun.asthmaproject.Utils.LocationService;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePal;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/2/22.
 */

public class MyApp extends Application {
    private static Context context;
    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        //获取全局Context
        context = getApplicationContext();
        //初始化LitePal
        LitePal.initialize(context);
        //初始化Http
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


    public static Context getContext() {
        return context;
    }
}
