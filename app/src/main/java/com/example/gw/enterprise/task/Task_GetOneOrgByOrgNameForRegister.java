package com.example.gw.enterprise.task;

import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by gw on 2018/10/8.
 */

public class Task_GetOneOrgByOrgNameForRegister extends BaseRequestor{
    public String orgName;

    @Override
    public Object execute() {
        RequestBody body = new FormBody.Builder()
                .add("orgName", orgName)
                .build();
        return CommnAction.request(body, "org/getOneOrgByOrgNameForRegister.do");
    }
}
