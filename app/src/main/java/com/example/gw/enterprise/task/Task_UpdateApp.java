package com.example.gw.enterprise.task;


import com.example.gw.enterprise.BuildConfig;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by gw on 2018/5/11.
 */

public class Task_UpdateApp extends BaseRequestor {
    public String version;

    @Override
    public Object execute() {
        RequestBody body = new FormBody.Builder()
                .add("version", version)
                .add("type", BuildConfig.Type)
                .add("clientType", BuildConfig.ClientType)
                .build();
        return CommnAction.request(body, "comm/checkVersion.do");
    }
}
