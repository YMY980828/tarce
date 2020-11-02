package com.example.gw.enterprise.common.base;

import com.example.gw.enterprise.BuildConfig;

/**
 * Created by gw on 15/8/18.
 */
public class BaseInfo {

    public static String getJsonURL() {
        return BuildConfig.BASE_SERVER_URL;//正式服务器
//        return "http://121.43.198.37:8080/testwebapp/desk/";//测试服务器
//        return "http://192.168.1.119:8080/webapp/desk/";//测试服务器
    }
}
