package com.example.gw.enterprise.common.action;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.service.DownloadService;
import com.example.gw.enterprise.common.utils.AppUtil;
import com.example.gw.enterprise.common.utils.ToastUtil;
import com.example.gw.enterprise.model.UpdateModel;
import com.example.gw.enterprise.task.Task_UpdateApp;
import com.google.gson.Gson;

import java.lang.reflect.Field;


/**
 * Created by gw on 15/9/21.
 */
public class UpdateAction {

    public static String getAppVersion() {
        return getVersionName(AppUtil.getApplicationContext());
    }

    public static String getUpdateFilePath(String version) {
        return AppUtil.getStoragePath() + "/update/" + "updateV" + version + ".apk";
    }

    public static String getVersionName(Context con) {
        PackageManager packageManager = con.getPackageManager();
        PackageInfo packInfo = null;

        try {
            packInfo = packageManager.getPackageInfo(con.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
        }

        String version = packInfo.versionName;
        return version;
    }

    public static void updateAction(final Context context, final boolean ischeck) {
        Task_UpdateApp task = new Task_UpdateApp();
        task.version = UpdateAction.getAppVersion();
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, context)) {
                    String msg = CommnAction.getInfo2(obj);
                    Gson gson = new Gson();
                    final UpdateModel updateModel = gson.fromJson(msg, UpdateModel.class);
                    if ("1".equals(updateModel.hasNew)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(updateModel.content);
                        builder.setTitle("新版本V" + updateModel.newVersion);
                        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if ("1".equals(updateModel.isForce)) {
                                    try {
                                        Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialogInterface, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                String url = updateModel.url;
                                Intent service = new Intent(context, DownloadService.class);
                                service.putExtra(DownloadService.INTENT_URL, url);
                                context.startService(service);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if ("1".equals(updateModel.isForce)) {
                                    try {
                                        Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialogInterface, false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    ToastUtil.showShort("请更新到最新版本");
                                }
                            }
                        });
                        builder.setCancelable(false);
                        builder.create().show();
                    } else {
                        if (ischeck) {

                            ToastUtil.showShort("已是最新版本无需更新");
                        }
                    }
                }
            }
        };
        task.start();
    }
}
