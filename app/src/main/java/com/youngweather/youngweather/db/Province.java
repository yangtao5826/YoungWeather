package com.youngweather.youngweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {

    private int id;//

    private String provinceName;//省份名称

    private int provinceCode;//省份代码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
