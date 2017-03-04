package com.test.requestjsontest.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 瞿康宁 on 2017/2/23.
 */

public class JsonData extends DataSupport{

    private int id;
    private String code;
    private String city;
    private String province;
    private String leader;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }
}
