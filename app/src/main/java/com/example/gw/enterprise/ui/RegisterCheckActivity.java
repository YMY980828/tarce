package com.example.gw.enterprise.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gw.enterprise.R;
import com.example.gw.enterprise.common.base.BaseActivity;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.utils.ToastUtil;
import com.example.gw.enterprise.model.OrgNameForRegisterModel;
import com.example.gw.enterprise.model.UpdateModel;
import com.example.gw.enterprise.task.Task_GetOneOrgByOrgNameForRegister;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import HPRTAndroidSDKA300.HPRTPrinterHelper;
import HPRTAndroidSDKA300.PublicFunction;
import butterknife.InjectView;
import hprt.Activity_DeviceList;
import hprt.checkClick;

/**
 * Created by gw on 2018/10/8.
 */

public class RegisterCheckActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.etOrgName)
    EditText etOrgName;
    @InjectView(R.id.tvSubmit)
    TextView tvSubmit;
    private String orgName;
    //蓝牙打印
    private Context thisCon = null;
    private BluetoothAdapter mBluetoothAdapter;
    private PublicFunction PFun = null;
    public static String paper = "0";
    private Handler handler;
    private ProgressDialog dialog;
    private String ConnectType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.register_check_activity);
        getNbBar().setNBTitle("用户注册");
        tvSubmit.setOnClickListener(this);
        InitSetting();
        //Enable Bluetooth
        EnableBluetooth();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Toast.makeText(thisCon, "succeed", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    Toast.makeText(thisCon, "failure", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }
        };
    }


    private void InitSetting() {
        thisCon = this.getApplicationContext();
        PFun = new PublicFunction(thisCon);
        String paper = PFun.ReadSharedPreferencesData("papertype");
        if (!"".equals(paper)) {
            RegisterCheckActivity.paper = paper;
        }
    }

    //EnableBluetooth
    private boolean EnableBluetooth() {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } else {
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString());
        }
        return bRet;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String paper = PFun.ReadSharedPreferencesData("papertype");
        if (!"".equals(paper)) {
            RegisterCheckActivity.paper = paper;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            String strIsConnected;
            switch (resultCode) {
                case HPRTPrinterHelper.ACTIVITY_CONNECT_BT:
                    int result = data.getExtras().getInt("is_connected");
                    if (result == 0) {
                        //连接成功
                        ToastUtil.showShort("连接成功");
                        TTexpress();
                    } else {
                        //连接失败
                        ToastUtil.showShort("连接失败");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void TTexpress() {
        try {
            HPRTPrinterHelper.printAreaSize("0", "320", "200", "340", "1");
            HPRTPrinterHelper.Align(HPRTPrinterHelper.CENTER);
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "4", "0", "0", "5", "国家农产品质量安全追溯");
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "24", "0", "0", "50", "追溯凭证");
            HPRTPrinterHelper.Align(HPRTPrinterHelper.LEFT);
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "24", "0", "150", "80", "商品名称：sas");
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "24", "0", "150", "110", "联系人：assa");
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "24", "0", "150", "140", "手机:12556884122");
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "24", "0", "60", "170", "产品追溯码：0123456789012345678901234");
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "24", "0", "60", "200", "凭证出具单位：sadadsaddsad");
            HPRTPrinterHelper.PrintQR(HPRTPrinterHelper.BARCODE, "60", "80", "2", "4", "ABC123");
            if ("1".equals(RegisterCheckActivity.paper)) {
                HPRTPrinterHelper.Form();
            }
            HPRTPrinterHelper.Print();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickWIFI ")).append(e.getMessage()).toString());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tvSubmit) {
//            orgName = etOrgName.getText().toString();
//            if (TextUtils.isEmpty(orgName)) {
//                ToastUtil.showShort("请填写主体名称");
//                return;
//            }
//            getOneOrgByOrgNameForRegister();
            if (!checkClick.isClickEvent()) return;
            if (!HPRTPrinterHelper.IsOpened()) {
                connectBT();
                return;
            } else {
                TTexpress();
            }

        }
    }

    private void connectBT() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(RegisterCheckActivity.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RegisterCheckActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
            } else {
                //具有权限
                ConnectType = "Bluetooth";
                Intent serverIntent = new Intent(thisCon, Activity_DeviceList.class);
                startActivityForResult(serverIntent, HPRTPrinterHelper.ACTIVITY_CONNECT_BT);
                return;
            }
        } else {
            //系统不高于6.0直接执行
            ConnectType = "Bluetooth";
            Intent serverIntent = new Intent(thisCon, Activity_DeviceList.class);
            startActivityForResult(serverIntent, HPRTPrinterHelper.ACTIVITY_CONNECT_BT);
        }
    }

    private void getOneOrgByOrgNameForRegister() {
        Task_GetOneOrgByOrgNameForRegister task = new Task_GetOneOrgByOrgNameForRegister();
        task.orgName = orgName;
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    String msg = CommnAction.getInfo2(obj);
                    Gson gson = new Gson();
                    OrgNameForRegisterModel model = gson.fromJson(msg, OrgNameForRegisterModel.class);
                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(intent);
                }
            }
        };
        task.start();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            HPRTPrinterHelper.PortClose();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
