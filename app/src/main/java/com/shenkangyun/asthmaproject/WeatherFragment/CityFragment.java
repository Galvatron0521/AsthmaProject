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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shenkangyun.asthmaproject.Activity.WeatherActivity;
import com.shenkangyun.asthmaproject.Adapter.CityAdapter;
import com.shenkangyun.asthmaproject.BaseFolder.Const;
import com.shenkangyun.asthmaproject.BaseFolder.MyApp;
import com.shenkangyun.asthmaproject.BeanFolder.AirEntity;
import com.shenkangyun.asthmaproject.DBFolder.CityDB;
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

public class CityFragment extends Fragment {
    @BindView(R.id.tv_province)
    TextView tvProvince;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.province_toolbar)
    Toolbar Toolbar;
    private int Id;
    private FragmentManager fragmentManager;
    private List<CityDB> cityDBList;
    private ProgressDialog progressDialog;

    private WeatherActivity weatherActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public, container, false);
        ButterKnife.bind(this, view);
        weatherActivity = (WeatherActivity) getActivity();
        initBundleData();
        initFindDB();
        return view;
    }

    private void initBundleData() {
        Bundle bundle = getArguments();
        Id = bundle.getInt("ProvinceID");
        String name = bundle.getString("ProvinceName");
        initToolBar(name);
    }

    private void initFindDB() {
        final List<CityDB> cityDBs = LitePal.where("provinceCode = ?", String.valueOf(Id)).find(CityDB.class);
        if (cityDBs.isEmpty()) {
            initHttp();
        } else {
            CityAdapter cityAdapter = new CityAdapter(getActivity(), cityDBs);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            recycler.setLayoutManager(manager);
            recycler.setAdapter(cityAdapter);
            cityAdapter.setOnItemClick(new CityAdapter.onItemClick() {
                @Override
                public void onItemClick(int position) {
                    CountryFragment fragment_country = new CountryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", cityDBs.get(position).getCityCode());
                    bundle.putInt("provinceCode", cityDBs.get(position).getProvinceCode());
                    bundle.putString("cityName", cityDBs.get(position).getName());
                    fragment_country.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_change, fragment_country);
                    transaction.commit();
                }
            });

        }
    }

    private void initHttp() {
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请稍等...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpUtils
                .get()
                .url(Const.URL_CITY + Id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                CityDB cityDB = new CityDB();
                                cityDB.setCityCode(jsonObject.getInt("id"));
                                cityDB.setName(jsonObject.getString("name"));
                                cityDB.setProvinceCode(Id);
                                cityDB.save();
                                cityDBList.add(cityDB);
                            }
                            CityAdapter cityAdapter = new CityAdapter(getActivity(), cityDBList);
                            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                            recycler.setLayoutManager(manager);
                            recycler.setAdapter(cityAdapter);
                            progressDialog.dismiss();
                            cityAdapter.setOnItemClick(new CityAdapter.onItemClick() {
                                @Override
                                public void onItemClick(int position) {
                                    String cityName = cityDBList.get(position).getName();
                                    CountryFragment fragment_country = new CountryFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("id", cityDBList.get(position).getCityCode());
                                    bundle.putInt("provinceCode", cityDBList.get(position).getProvinceCode());
                                    bundle.putString("cityName", cityName);
                                    fragment_country.setArguments(bundle);

                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    transaction.replace(R.id.frame_change, fragment_country);
                                    transaction.commit();

                                    getAirQualityInfo(cityName);

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                });
    }

    private void getAirQualityInfo(String cityName) {
        OkHttpUtils
                .get()
                .url(Const.URL_AIR + cityName)
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
                        AirEntity airEntity = sGson.fromJson(response, AirEntity.class);
                        weatherActivity.getAir(airEntity);
                        progressDialog.dismiss();
                    }
                });
    }

    private void initToolBar(String name) {
        progressDialog = new ProgressDialog(getActivity());
        cityDBList = new ArrayList<>();
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
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_change, new ProvinceFragment());
                transaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
