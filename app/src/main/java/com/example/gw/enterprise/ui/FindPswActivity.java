package com.example.gw.enterprise.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gw.enterprise.R;
import com.example.gw.enterprise.common.base.BaseActivity;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.component.NiceSpinner;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.utils.ToastUtil;
import com.example.gw.enterprise.task.Task_FindLoginInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.InjectView;

/**
 * Created by gw on 2018/10/16.
 */

public class FindPswActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.spType)
    NiceSpinner spType;
    @InjectView(R.id.etOrgName)
    EditText etOrgName;
    @InjectView(R.id.etLegalPersonName)
    EditText etLegalPersonName;
    @InjectView(R.id.etLegalPersonMobile)
    EditText etLegalPersonMobile;
    @InjectView(R.id.etPsw)
    EditText etPsw;
    @InjectView(R.id.etPsw2)
    EditText etPsw2;
    @InjectView(R.id.linPsw)
    LinearLayout linPsw;
    @InjectView(R.id.tvSubmit)
    TextView tvSubmit;
    private ArrayList<String> typeData = new ArrayList<String>();
    private String type, orgName, legalPersonName, legalPersonMobile, newPassword, newPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_findpsw);
        getNbBar().setNBTitle("找回账号/密码");
        initUI();
    }

    private void initUI() {
        tvSubmit.setOnClickListener(this);
        //主体类型
        typeData.clear();
        typeData.add("请选择");
        typeData.add("找回账号");
        typeData.add("找回密码");
        spType.attachDataSource(typeData);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    linPsw.setVisibility(View.VISIBLE);
                } else {
                    linPsw.setVisibility(View.GONE);
                }
                type = position + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tvSubmit) {
            orgName = etOrgName.getText().toString();
            legalPersonName = etLegalPersonName.getText().toString();
            legalPersonMobile = etLegalPersonMobile.getText().toString();
            newPassword = etPsw.getText().toString();
            newPassword2 = etPsw2.getText().toString();
            if ("0".equals(type) || TextUtils.isEmpty(type)) {
                ToastUtil.showShort("请选择类型");
                return;
            }
            if (TextUtils.isEmpty(orgName)) {
                ToastUtil.showShort("请填写主体名称");
                return;
            }
            if (TextUtils.isEmpty(legalPersonName)) {
                ToastUtil.showShort("请填写法人姓名");
                return;
            }
            if (TextUtils.isEmpty(legalPersonMobile)) {
                ToastUtil.showShort("请填写法人电话");
                return;
            }
            if (!checkPhone(legalPersonMobile)) {
                ToastUtil.showShort("法人电话填写有误");
                return;
            }
            if ("2".equals(type)) {
                if (TextUtils.isEmpty(newPassword)) {
                    ToastUtil.showShort("请填写新密码");
                    return;
                }
                if (!newPassword.equals(newPassword2)) {
                    ToastUtil.showShort("密码输入不一致，请重新确认");
                    return;
                }
            }
            findLoginInfo();
        }
    }

    private void findLoginInfo() {
        Task_FindLoginInfo task = new Task_FindLoginInfo();
        task.type = type;
        task.orgName = orgName;
        task.legalPersonName = legalPersonName;
        task.legalPersonMobile = legalPersonMobile;
        task.newPassword = newPassword;
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    JsonObject jsonObject = new JsonParser().parse(obj.toString()).getAsJsonObject();
                    if ("1".equals(type)) {
                        String userName = jsonObject.get("userName").getAsString();
                        showDialog("登录账号：" + userName);
                    } else {
                        ToastUtil.showShort("密码修改成功");
                        finish();
                    }

                }
            }
        };
        task.start();
    }

    public void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_normal, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText("账号找回");
        TextView tvMsg = (TextView) view.findViewById(R.id.message);
        tvMsg.setText(msg);
        Button btnQr = (Button) view.findViewById(R.id.positiveButton);
        btnQr.setText("知道了");
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });
        Button btnQx = (Button) view.findViewById(R.id.negativeButton);
        btnQx.setVisibility(View.GONE);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private boolean checkPhone(String str) {
        Pattern pattern = Pattern.compile("(^(0[0-9]{2,3}[/-]?)([2-9][0-9]{6,7})+(\\/-[0-9]{1,4})?$)|(13|14|15|17|18|19)[0-9]{9}");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
