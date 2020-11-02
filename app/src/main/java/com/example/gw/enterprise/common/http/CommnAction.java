package com.example.gw.enterprise.common.http;

import android.content.Context;

import com.example.gw.enterprise.common.base.BaseInfo;
import com.example.gw.enterprise.common.utils.ToastUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.RequestBody;

/**
 * Created by gw on 15/11/27.
 */
public class CommnAction {

    public static boolean CheckY(Object obj, Context con) {
        if (obj == null) {
            ToastUtil.showShort("网络异常");
            return false;
        } else if ("{}".equals(obj.toString()) || "".equals(obj.toString())) {
            ToastUtil.showShort("数据异常");
            return false;
        }
        JsonObject jsonObj = new JsonParser().parse(obj.toString()).getAsJsonObject();
        String state = jsonObj.get("state").getAsString();
        String msg = jsonObj.get("msg").getAsString();
        if (con != null) {
            if ("Y".equals(state)) {
                return true;
            } else {
                ToastUtil.showShort(msg);
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean CheckState(Object obj, Context con) {
        if (obj == null) {
            ToastUtil.showShort("网络异常");
            return false;
        } else if ("{}".equals(obj.toString()) || "".equals(obj.toString())) {
            ToastUtil.showShort("数据异常");
            return false;
        }
        JsonObject jsonObj = new JsonParser().parse(obj.toString()).getAsJsonObject();
        String state = jsonObj.get("status").getAsString();
        String msg = jsonObj.get("msg").getAsString();
        if (con != null) {
            if ("true".equals(state)) {
                return true;
            } else {
                ToastUtil.showShort(msg);
                return false;
            }
        } else {
            return false;
        }
    }

    public static String request(RequestBody body, String method) {
        String url = BaseInfo.getJsonURL();
        if (url.equals("")) {
            return null;
        }
        try {
            url += method;
            System.out.println(url);
            System.out.println(body);
            String bs = WebUtil.doPost(url, body);
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

    public static String getInfo(Object obj) {
        JsonObject jsonObj = new JsonParser().parse(obj.toString()).getAsJsonObject();
        JsonArray jsonArray = jsonObj.get("data").getAsJsonArray();
        String msg = jsonArray.toString();
        return msg;
    }

    public static String getInfo2(Object obj) {
        JsonObject jsonObj = new JsonParser().parse(obj.toString()).getAsJsonObject();
        JsonObject jsonObject = jsonObj.get("data").getAsJsonObject();
        String msg = jsonObject.toString();
        return msg;
    }
}
