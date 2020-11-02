package com.example.gw.enterprise.task;

import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by gw on 2018/8/29.
 */

public class Task_GetAllOrgNatures extends BaseRequestor{

    @Override
    public Object execute() {
        RequestBody body = new FormBody.Builder()
                .build();
        return CommnAction.request(body, "dic/getAllOrgNatures.do");
    }
}
