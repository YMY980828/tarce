package com.example.gw.enterprise.common.db;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by gw on 17/3/15.
 */
public class DBManager {

    public static void setOtherConfig(String key, String value) {
        try {
            OtherConfig otherConfig = new OtherConfig();
            otherConfig.setKey(key);
            otherConfig.setValue(value);
            otherConfig.saveOrUpdate("key=?", otherConfig.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getOtherConfig(String key) {
        String str = "";
        try {
            List<OtherConfig> configList = DataSupport.where("key = ?", key).find(OtherConfig.class);
            int i = configList.size();
            if (i == 0) {
                str = "";
            } else {
                str = configList.get(0).getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
