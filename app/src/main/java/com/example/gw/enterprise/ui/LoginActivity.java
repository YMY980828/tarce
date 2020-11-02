package com.example.gw.enterprise.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gw.enterprise.R;
import com.example.gw.enterprise.common.FileConfig;
import com.example.gw.enterprise.common.base.BaseActivity;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.db.DBManager;
import com.example.gw.enterprise.common.db.FrmConfigKeys;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.utils.ToastUtil;
import com.example.gw.enterprise.task.Task_Login;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by gw on 2018/8/29.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.user_name)
    EditText etName;
    @InjectView(R.id.password)
    EditText etPsw;
    @InjectView(R.id.tv_login)
    TextView tvLogin;
    @InjectView(R.id.tv_register)
    TextView tvRegister;
    @InjectView(R.id.tv_find_psw)
    TextView tvFindPsw;
    private String loginName, password;
    private static final String[] All_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int All_PERMISSIONS_CODE = 1;
    List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_login);
        getNbBar().hide();
        initUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
    }

    //申请权限
    private void requestPermission() {
        // 当API大于 23 时，才动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionList.clear();//清空没有通过的权限
            //逐个判断你要的权限是否已经通过
            for (int i = 0; i < All_PERMISSIONS.length; i++) {
                if (ContextCompat.checkSelfPermission(this, All_PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(All_PERMISSIONS[i]);//添加还未授予的权限
                }
            }
            //申请权限
            if (mPermissionList.size() > 0) {
                //有权限没有通过，需要申请
                ActivityCompat.requestPermissions(this, All_PERMISSIONS, All_PERMISSIONS_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case All_PERMISSIONS_CODE:
                //权限请求失败
                if (grantResults.length == All_PERMISSIONS.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //弹出对话框引导用户去设置
                            showDialog();
                            ToastUtil.showShort("请求权限被拒绝");
                            break;
                        }
                    }
                    //初始化文件夹创建
                    FileConfig.initFolders();
                }
                break;
        }
    }

    //弹出提示框
    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("追溯系统需要蓝牙、相机和读写权限，是否去设置？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToAppSetting();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void initUI() {
        tvRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        tvFindPsw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tvRegister) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (v == tvLogin) {
            loginName = etName.getText().toString().trim();
            password = etPsw.getText().toString().trim();
            if (TextUtils.isEmpty(loginName)) {
                ToastUtil.showShort("请输入用户名");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                ToastUtil.showShort("请输入密码");
                return;
            }
            login();
        } else if (v == tvFindPsw) {
            Intent intent = new Intent(this, FindPswActivity.class);
            startActivity(intent);
        }
    }

    private void login() {
        Task_Login task = new Task_Login();
        task.loginName = loginName;
        task.password = password;
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    JsonObject data = new JsonParser().parse(obj.toString()).getAsJsonObject();
                    String token = data.get("token").getAsString();
                    DBManager.setOtherConfig(FrmConfigKeys.token, token);
                    DBManager.setOtherConfig(FrmConfigKeys.loginResult, obj.toString());
                    Intent mintent = new Intent(getActivity(), TBSWebViewActivity.class);
                    startActivity(mintent);
                    finish();
                }
            }
        };
        task.start();
    }
}
