package com.example.gw.enterprise.task;

import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by gw on 2018/8/27.
 */

public class Task_GetUploadToken extends BaseRequestor {

    @Override
    public Object execute() {
        RequestBody body = new FormBody.Builder()
                .build();
        return CommnAction.request(body, "comm/getUploadToken.do");
    }
}
