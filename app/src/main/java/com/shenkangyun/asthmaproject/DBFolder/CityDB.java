package com.shenkangyun.asthmaproject.DBFolder;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Administrator on 2017/8/23.
 */

public class CityDB extends LitePalSupport {
    private int id;
    private String name;
    private int cityCode;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
