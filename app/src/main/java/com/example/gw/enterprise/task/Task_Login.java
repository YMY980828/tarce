package com.example.gw.enterprise.task;

import com.example.gw.enterprise.BuildConfig;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by gw on 2018/8/30.
 */

public class Task_Login extends BaseRequestor {
    public String loginName;
    public String password;

    @Override
    public Object execute() {
        RequestBody body = new FormBody.Builder()
                .add("loginName", loginName)
                .add("password", password)
                .add("type", BuildConfig.Type)
                .add("clientType", BuildConfig.ClientType)
                .build();
        return CommnAction.request(body, "auth/getToken.do");
    }
}