package com.example.gw.enterprise.task;

import android.content.SharedPreferences;

import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.db.DBManager;
import com.example.gw.enterprise.common.db.FrmConfigKeys;
import com.example.gw.enterprise.common.http.CommnAction;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Task_SavePrintRecord extends BaseRequestor {
    public String productName;
    public String productId;
    public String traceCode;
    public String printKindId;
    public String specification;
    public String count;

    @Override
    public Object execute() {

        RequestBody body = new FormBody.Builder()
                .add("productName", productName)
                .add("productId", productId)
                .add("traceCode", traceCode)
                .add("printKindId", printKindId)
                .add("specification", specification)
                .add("count", count)
                .add("macAddr", DBManager.getOtherConfig(FrmConfigKeys.macAddr))
                .add("deviceName", DBManager.getOtherConfig(FrmConfigKeys.deviceName))
                .build();
        return CommnAction.request(body, "org/addPrintRecord.do");
    }
}
