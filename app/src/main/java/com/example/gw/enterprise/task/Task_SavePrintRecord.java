package com.example.gw.enterprise.task;

import android.content.SharedPreferences;

import com.example.gw.enterprise.common.base.BaseInfo;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.db.DBManager;
import com.example.gw.enterprise.common.db.FrmConfigKeys;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.http.WebUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Task_SavePrintRecord extends BaseRequestor {
    public String productName;
    public String productId;
    public String traceCode;
    public String printKindId;
    public String specification;
    public String count;
    public String weight="";
    public String unitId="";
    public String orgProductGreedId="";
    @Override
    public Object execute() {
        String url = BaseInfo.getJsonURL();
        if (url.equals("")) {
            return null;
        }
        RequestBody body = new FormBody.Builder()
                .add("productName", productName)
                .add("weight", weight)
                .add("unitId", unitId)
                .add("orgProductGreedId", orgProductGreedId)
                .add("productId", productId)
                .add("traceCode", traceCode)
                .add("printKindId", printKindId)
                .add("specification", specification)
                .add("count", count)
                .add("macAddr", DBManager.getOtherConfig(FrmConfigKeys.macAddr))
                .add("deviceName", DBManager.getOtherConfig(FrmConfigKeys.deviceName))
                .build();
        try {
            url += "org/addPrintRecord.do";
            System.out.println(url);
            System.out.println(body);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request =  chain.request().newBuilder().addHeader("X-Token", DBManager.getOtherConfig(FrmConfigKeys.token)).build();
                            return chain.proceed(request);
                        }
                    })
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = client.newCall(request);
            Response execute = call.execute();
            String bs =  execute.body().string();
            if (bs != null) {
                System.out.println(bs);
            } else {
                System.out.println("接口异常,返回null");
            }
            return bs;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
