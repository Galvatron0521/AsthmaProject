package com.shenkangyun.asthmaproject.WeatherFragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shenkangyun.asthmaproject.Activity.WeatherActivity;
import com.shenkangyun.asthmaproject.Adapter.CountryAdapter;
import com.shenkangyun.asthmaproject.BaseFolder.Const;
import com.shenkangyun.asthmaproject.BaseFolder.MyApp;
import com.shenkangyun.asthmaproject.BeanFolder.WeatherEntity;
import com.shenkangyun.asthmaproject.DBFolder.CountryDB;
import com.shenkangyun.asthmaproject.DBFolder.ProvinceDB;
import com.shenkangyun.asthmaproject.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/8/23.
 */

public class CountryFragment extends Fragment {
    @BindView(R.id.tv_province)
    TextView tvProvince;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.province_toolbar)
    Toolbar Toolbar;
    private int Id, provinceId;
    private FragmentManager fragmentManager;
    private List<CountryDB> countryDBs;
    private ProgressDialog progressDialog;
    private WeatherActivity weatherActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public, container, false);
        ButterKnife.bind(this, view);
        weatherActivity = (WeatherActivity) getActivity();
        Bundle bundle = getArguments();
        Id = bundle.getInt("id");
        provinceId = bundle.getInt("provinceCode");
        String cityName = bundle.getString("cityName");
        initToolBar(cityName);
        initFindDB();
        return view;
    }

    private void initFindDB() {
        List<CountryDB> countryDBs = LitePal.where("cityCode = ?", String.valueOf(Id)).find(CountryDB.class);
        if (countryDBs.isEmpty()) {
            initHttp();
        } else {
            CountryAdapter countryAdapter = new CountryAdapter(getActivity(), countryDBs);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            recycler.setLayoutManager(manager);
            recycler.setAdapter(countryAdapter);
        }
    }

    private void initHttp() {
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请稍等...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpUtils
                .get()
                .url(Const.URL_CITY + provinceId + "/" + Id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                CountryDB countryDB = new CountryDB();
                                countryDB.setCityCode(Id);
                                countryDB.setName(jsonObject.getString("name"));
                                countryDB.setCountryCode(jsonObject.getInt("id"));
                                countryDB.setWeather_id(jsonObject.getString("weather_id"));
                                countryDB.save();
                                countryDBs.add(countryDB);
                            }
                            initRecyclerView(countryDBs);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                });
    }

    private void initRecyclerView(final List<CountryDB> countryDBs) {
        CountryAdapter countryAdapter = new CountryAdapter(getActivity(), countryDBs);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(manager);
        recycler.setAdapter(countryAdapter);
        progressDialog.dismiss();

        countryAdapter.setOnItemClick(new CountryAdapter.OnItemClick() {
            @Override
            public void itemClick(int position) {
                weatherActivity.close();
                //请求接口
                progressInit();
                OkHttpUtils
                        .get()
                        .url(Const.URL_WEATHER + countryDBs.get(position).getName())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                progressDialog.dismiss();
                                Toast.makeText(MyApp.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Gson sGson = new Gson();
                                WeatherEntity weatherEntity = sGson.fromJson(response, WeatherEntity.class);
                                weatherActivity.getWeather(weatherEntity);
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }

    public void progressInit() {
        //设置dialog标题内容
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请等待...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initToolBar(String name) {
        progressDialog = new ProgressDialog(getActivity());
        countryDBs = new ArrayList<>();
        setHasOptionsMenu(true);
        fragmentManager = getFragmentManager();
        ((AppCompatActivity) getActivity()).setSupportActionBar(Toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tvProvince.setText(name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CityFragment fragment_city = new CityFragment();
                List<ProvinceDB> provinceDBs = LitePal.where("ProvinceID = ?", String.valueOf(provinceId)).find(ProvinceDB.class);
                int cityCode = provinceDBs.get(0).getProvinceID();
                String name = provinceDBs.get(0).getProvinceName();
                Bundle bundle = new Bundle();
                bundle.putInt("ProvinceID", cityCode);
                bundle.putString("ProvinceName", name);
                fragment_city.setArguments(bundle);
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_change, fragment_city);
                transaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
