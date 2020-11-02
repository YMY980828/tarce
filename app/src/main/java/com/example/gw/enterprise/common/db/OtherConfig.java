package com.example.gw.enterprise.common.db;

import org.litepal.crud.DataSupport;

/**
 * Created by gw on 17/3/15.
 */
public class OtherConfig extends DataSupport {
    private String key;
    private String value;

    //getter和setter
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
