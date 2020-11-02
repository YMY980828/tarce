package com.example.gw.enterprise;

import android.content.Context;
import android.util.Log;

import com.example.gw.enterprise.common.FileConfig;
import com.example.gw.enterprise.common.base.BaseApplication;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import org.litepal.LitePalApplication;

/**
 * Created by gw on 2018/8/27.
 */

public class FrmApplication extends BaseApplication {

    public static Context applicationContext;
    private static FrmApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        //腾讯bugly
        CrashReport.initCrashReport(getApplicationContext(), "9b6ec368ec", true);

        applicationContext = this;
        instance = this;
        //初始化文件夹创建
        FileConfig.initFolders();
        //系统异常捕获
        Thread.setDefaultUncaughtExceptionHandler(handler);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    public static FrmApplication getInstance() {
        return instance;
    }

    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, final Throwable ex) {
            ex.printStackTrace();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    };


}
