package com.shenkangyun.asthmaproject.DBFolder;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Administrator on 2017/8/23.
 */

public class ProvinceDB extends LitePalSupport {
    private int id;
    private int ProvinceID;
    private String ProvinceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(int provinceID) {
        ProvinceID = provinceID;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }
}
