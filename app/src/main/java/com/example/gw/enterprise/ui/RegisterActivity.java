package com.example.gw.enterprise.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gw.enterprise.BuildConfig;
import com.example.gw.enterprise.R;
import com.example.gw.enterprise.common.base.BaseActivity;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.component.NiceSpinner;
import com.example.gw.enterprise.common.component.NoArrowSpinner;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.utils.CheckUtil;
import com.example.gw.enterprise.common.utils.DisplayUtil;
import com.example.gw.enterprise.common.utils.ImageLoaderUtils;
import com.example.gw.enterprise.common.utils.ToastUtil;
import com.example.gw.enterprise.model.AreaModel;
import com.example.gw.enterprise.model.OrgNatureModel;
import com.example.gw.enterprise.photoPicker.ImageLoader;
import com.example.gw.enterprise.photoPicker.ImgSelActivity;
import com.example.gw.enterprise.photoPicker.ImgSelConfig;
import com.example.gw.enterprise.task.Task_AddOrganizationInfo;
import com.example.gw.enterprise.task.Task_GetAllOrgNatures;
import com.example.gw.enterprise.task.Task_GetAreasByParAreaCode;
import com.example.gw.enterprise.task.Task_GetUploadToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.InjectView;

/**
 * Created by gw on 2018/8/29.
 */

public class RegisterActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    @InjectView(R.id.etRegisteredNumber)
    EditText etRegisteredNumber;
    @InjectView(R.id.etCustomUserName)
    EditText etCustomUserName;
    @InjectView(R.id.etOrgName)
    EditText etOrgName;
    @InjectView(R.id.etAddress)
    EditText etAddress;
    @InjectView(R.id.etContactPersonName)
    EditText etContactPersonName;
    @InjectView(R.id.etContactPersonIdCard)
    EditText etContactPersonIdCard;
    @InjectView(R.id.etContactPersonMobile)
    EditText etContactPersonMobile;
    @InjectView(R.id.etLegalPersonName)
    EditText etLegalPersonName;
    @InjectView(R.id.etLegalPersonIdCard)
    EditText etLegalPersonIdCard;
    @InjectView(R.id.etLegalPersonMobile)
    EditText etLegalPersonMobile;
    @InjectView(R.id.spOrgNature)
    NiceSpinner spOrgNature;
    @InjectView(R.id.spOrgType)
    NiceSpinner spOrgType;
    @InjectView(R.id.spAreaCodeCity)
    NoArrowSpinner spAreaCodeCity;
    @InjectView(R.id.spAreaCodeDistrict)
    NoArrowSpinner spAreaCodeDistrict;
    @InjectView(R.id.spAreaCodeSubdistrict)
    NoArrowSpinner spAreaCodeSubdistrict;
    @InjectView(R.id.linAddPic)
    LinearLayout linAddPic;
    @InjectView(R.id.tvPic)
    TextView tvPic;
    @InjectView(R.id.ivAddPic)
    ImageView ivAddPic;
    @InjectView(R.id.ivDelete)
    ImageView ivDelete;
    private ArrayList<String> orgTypeData = new ArrayList<String>();
    private ArrayList<String> orgNatureData = new ArrayList<String>();
    private ArrayList<String> areaCityData = new ArrayList<String>();
    private ArrayList<String> areaDistrictData = new ArrayList<String>();
    private ArrayList<String> areaSubdistrictData = new ArrayList<String>();
    private List<OrgNatureModel> orgNatureList = new ArrayList<>();
    private List<AreaModel> areaCityList = new ArrayList<>();
    private List<AreaModel> areaDistrictList = new ArrayList<>();
    private List<AreaModel> areaSubdistrictList = new ArrayList<>();
    private String orgType, orgNature, areaCodeCity, areaCodeDistrict, areaCodeSubdistrict;
    private String registeredNumber, customUserName, orgName, address, contactPersonName, contactPersonIdCard, contactPersonMobile, legalPersonName, legalPersonIdCard, legalPersonMobile, legalPersonIdCardFront, businessLicense;
    private String areaCode, imagePath;
    private int REQUEST_CODE = 120;
    private String uploadPath, imgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_register);
        getNbBar().setNBTitle("用户注册");
        getNbBar().nbRightText.setText("提交");
        initUI();
        getAllOrgNatures();
        getArea(1);
    }

    private void initUI() {
        legalPersonIdCardFront = "";
        businessLicense = "";
        areaCode = BuildConfig.AreaCode;
        //主体类型
        orgTypeData.clear();
        orgTypeData.add("生产主体");
        orgTypeData.add("经营主体");
        orgTypeData.add("生产经营主体");
        spOrgType.attachDataSource(orgTypeData);
        spOrgNature.setOnItemSelectedListener(this);
        spOrgType.setOnItemSelectedListener(this);
        spAreaCodeCity.setOnItemSelectedListener(this);
        spAreaCodeDistrict.setOnItemSelectedListener(this);
        spAreaCodeSubdistrict.setOnItemSelectedListener(this);
        ivAddPic.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
    }

    private void getAllOrgNatures() {
        Task_GetAllOrgNatures task = new Task_GetAllOrgNatures();
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    String msg = CommnAction.getInfo(obj);
                    orgNatureList.clear();
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<OrgNatureModel>>() {
                    }.getType();
                    List<OrgNatureModel> list = gson.fromJson(msg, type);
                    orgNatureList.addAll(list);
                    initOrgNature();
                }
            }
        };
        task.start();
    }

    private void initOrgNature() {
        //组织形式
        orgNatureData.clear();
        for (int i = 0; i < orgNatureList.size(); i++) {
            orgNatureData.add(orgNatureList.get(i).orgNatureName);
        }
        spOrgNature.attachDataSource(orgNatureData);
    }

    private void getArea(final int flag) {
        Task_GetAreasByParAreaCode task = new Task_GetAreasByParAreaCode();
        task.areaCode = areaCode;
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    String msg = CommnAction.getInfo(obj);
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<AreaModel>>() {
                    }.getType();
                    List<AreaModel> list = gson.fromJson(msg, type);
                    if (flag == 1) {
                        areaCityList.clear();
                        areaCityList.addAll(list);
                        initAreaCity();
                    } else if (flag == 2) {
                        areaDistrictList.clear();
                        areaDistrictList.addAll(list);
                        initDistrict();
                    } else {
                        areaSubdistrictList.clear();
                        areaSubdistrictList.addAll(list);
                        initSubdistrict();
                    }
                }
            }
        };
        task.start();
    }

    private void initSubdistrict() {
        //街道
        areaSubdistrictData.clear();
        for (int i = 0; i < areaSubdistrictList.size(); i++) {
            areaSubdistrictData.add(areaSubdistrictList.get(i).areaName);
        }
        spAreaCodeSubdistrict.attachDataSource(areaSubdistrictData);
    }

    private void initDistrict() {
        //区县
        areaDistrictData.clear();
        for (int i = 0; i < areaDistrictList.size(); i++) {
            areaDistrictData.add(areaDistrictList.get(i).areaName);
        }
        spAreaCodeDistrict.attachDataSource(areaDistrictData);
    }

    private void initAreaCity() {
        //城市
        areaCityData.clear();
        for (int i = 0; i < areaCityList.size(); i++) {
            areaCityData.add(areaCityList.get(i).areaName);
        }
        spAreaCodeCity.attachDataSource(areaCityData);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spOrgNature:
                orgNature = orgNatureList.get(position).orgNatureId + "";
                if ("3".equals(orgNature)) {
                    tvPic.setText("身份证正面");
                } else {
                    tvPic.setText("营业执照");
                }
                linAddPic.setVisibility(View.VISIBLE);
                break;
            case R.id.spOrgType:
                orgType = position + "";
                break;
            case R.id.spAreaCodeCity:
                areaCodeCity = areaCityList.get(position).areaCode + "";
                areaCode = areaCodeCity;
                areaCodeDistrict = "";
                areaDistrictList.clear();
                areaDistrictData.clear();
                spAreaCodeDistrict.setText("区县");
                areaCodeSubdistrict = "";
                areaSubdistrictList.clear();
                areaSubdistrictData.clear();
                spAreaCodeSubdistrict.setText("街道");
                getArea(2);
                break;
            case R.id.spAreaCodeDistrict:
                areaCodeDistrict = areaDistrictList.get(position).areaCode + "";
                areaCode = areaCodeDistrict;
                areaCodeSubdistrict = "";
                areaSubdistrictList.clear();
                areaSubdistrictData.clear();
                spAreaCodeSubdistrict.setText("街道");
                getArea(3);
                break;
            case R.id.spAreaCodeSubdistrict:
                areaCodeSubdistrict = areaSubdistrictList.get(position).areaCode + "";
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onNBRight() {
        super.onNBRight();
        registeredNumber = etRegisteredNumber.getText().toString().replace(" ", "");
        customUserName = etCustomUserName.getText().toString().replace(" ", "");
        orgName = etOrgName.getText().toString().replace(" ", "");
        address = etAddress.getText().toString().replace(" ", "");
        contactPersonName = etContactPersonName.getText().toString();
        contactPersonIdCard = etContactPersonIdCard.getText().toString().replace(" ", "");
        contactPersonMobile = etContactPersonMobile.getText().toString().replace(" ", "");
        legalPersonName = etLegalPersonName.getText().toString().replace(" ", "");
        legalPersonIdCard = etLegalPersonIdCard.getText().toString().replace(" ", "");
        legalPersonMobile = etLegalPersonMobile.getText().toString().replace(" ", "");
        if (TextUtils.isEmpty(registeredNumber)) {
            ToastUtil.showShort("请填写注册号");
            return;
        }
        if (TextUtils.isEmpty(customUserName)) {
            ToastUtil.showShort("请填写用户名");
            return;
        }
        if (TextUtils.isEmpty(orgName)) {
            ToastUtil.showShort("请填写主体名称");
            return;
        }
        if (TextUtils.isEmpty(orgNature)) {
            ToastUtil.showShort("请选择组织形式");
            return;
        }
        if (TextUtils.isEmpty(orgType)) {
            ToastUtil.showShort("请选择主体类型");
            return;
        }
        if (TextUtils.isEmpty(areaCodeDistrict)) {
            ToastUtil.showShort("请选择地区");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtil.showShort("请填写详细地址");
            return;
        }
        if (TextUtils.isEmpty(contactPersonName)) {
            ToastUtil.showShort("请填写联系人姓名");
            return;
        }
        if (TextUtils.isEmpty(contactPersonIdCard)) {
            ToastUtil.showShort("请填写联系人身份证");
            return;
        }
        if (TextUtils.isEmpty(contactPersonMobile)) {
            ToastUtil.showShort("请填写联系人电话");
            return;
        }
        if (TextUtils.isEmpty(legalPersonName)) {
            ToastUtil.showShort("请填写法人姓名");
            return;
        }
        if (TextUtils.isEmpty(legalPersonIdCard)) {
            ToastUtil.showShort("请填写法人身份证");
            return;
        }
        if (TextUtils.isEmpty(legalPersonMobile)) {
            ToastUtil.showShort("请填写法人电话");
            return;
        }
        if (!checkRegisterNumber(registeredNumber)) {
            ToastUtil.showShort("注册号填写有误");
            return;
        }
        if (!checkName(customUserName)) {
            ToastUtil.showShort("用户名填写有误");
            return;
        }
        if (!CheckUtil.IDCardValidate(contactPersonIdCard)) {
            ToastUtil.showShort("联系人身份证填写有误");
            return;
        }
        if (!CheckUtil.IDCardValidate(legalPersonIdCard)) {
            ToastUtil.showShort("法人身份证填写有误");
            return;
        }
        if (!checkPhone(contactPersonMobile)) {
            ToastUtil.showShort("联系人电话填写有误");
            return;
        }
        if (!checkPhone(legalPersonMobile)) {
            ToastUtil.showShort("法人电话填写有误");
            return;
        }
        if (TextUtils.isEmpty(imagePath)) {
            if ("3".equals(orgNature)) {
                ToastUtil.showShort("请上传身份证正面");
            } else {
                ToastUtil.showShort("请上传营业执照");
            }
            return;
        }
        new TaskThread().start();
    }

    class TaskThread extends Thread {
        public void run() {
            System.out.println("-->做一些耗时的任务");
            try {
                compressPic();
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
        }
    }

    private void compressPic() {
        uploadPath = compressPath(imagePath);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    System.out.println("-->回到主线程刷新ui任务");
                    getUploadToken();
                }
                break;

                default:
                    break;
            }
        }

        ;
    };

    private void getUploadToken() {
        Task_GetUploadToken task = new Task_GetUploadToken();
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    JsonObject jsonObj = new JsonParser().parse(obj.toString()).getAsJsonObject();
                    String token = jsonObj.get("data").getAsString();
                    imgUrl = jsonObj.get("data2").getAsString();
                    uploadFile(token);
                }
            }
        };
        task.start();
    }

    private void uploadFile(String token) {
        showLoading();
        UploadManager uploadManager = new UploadManager();
        // 设置图片名字
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String key = "icon_" + sdf.format(new Date()) + "-" + Math.round(Math.random() * 1000) + ".jpg";
        uploadManager.put(uploadPath, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                // 上传成功后将key值上传到自己的服务器
                hideLoading();
                if (info.isOK()) {
                    if ("3".equals(orgNature)) {
                        legalPersonIdCardFront = imgUrl + "/" + key;
                    } else {
                        businessLicense = imgUrl + "/" + key;
                    }
                    addOrganizationInfo();
                }
            }
        }, null);
    }

    private void addOrganizationInfo() {
        Task_AddOrganizationInfo task = new Task_AddOrganizationInfo();
        task.registeredNumber = registeredNumber;
        task.customUserName = customUserName;
        task.orgName = orgName;
        task.address = address;
        task.contactPersonName = contactPersonName;
        task.contactPersonIdCard = contactPersonIdCard;
        task.contactPersonMobile = contactPersonMobile;
        task.legalPersonName = legalPersonName;
        task.legalPersonIdCard = legalPersonIdCard;
        task.legalPersonMobile = legalPersonMobile;
        task.orgType = orgType;
        task.orgNature = orgNature;
        task.areaCodeCity = areaCodeCity;
        task.areaCodeDistrict = areaCodeDistrict;
        task.areaCodeSubdistrict = areaCodeSubdistrict;
        task.legalPersonIdCardFront = legalPersonIdCardFront;
        task.businessLicense = businessLicense;
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    JsonObject jsonObject = new JsonParser().parse(obj.toString()).getAsJsonObject();
                    String data = jsonObject.get("data").getAsString();
                    showDialog("登录账号：" + data + "\n默认密码：123456\n请等待审核！");
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

    private boolean checkRegisterNumber(String str) {
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z]{15}$|^[0-9a-zA-Z]{18}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private boolean checkPhone(String str) {
        Pattern pattern = Pattern.compile("(^(0[0-9]{2,3}[/-]?)([2-9][0-9]{6,7})+(\\/-[0-9]{1,4})?$)|(13|14|15|17|18|19)[0-9]{9}");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private boolean checkName(String str) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    @Override
    public void onClick(View v) {
        if (v == ivAddPic) {
            if (TextUtils.isEmpty(imagePath)) {
                choosePic();
            } else {
                String[] imagePaths = new String[1];
                imagePaths[0] = imagePath;
                DisplayUtil.showFullImage(getActivity(), imagePaths, 0);
            }

        } else if (v == ivDelete) {
            imagePath = "";
            ImageLoaderUtils.display(getActivity(), ivAddPic, R.drawable.addphoto);
            ivDelete.setVisibility(View.GONE);
        }
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            ImageLoaderUtils.display(context, imageView, path);
        }
    };

    private void choosePic() {
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(true)
                // 确定按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                .titleBgColor(ContextCompat.getColor(this, R.color.main_color))
                // 使用沉浸式状态栏
                .statusBarColor(ContextCompat.getColor(this, R.color.main_color))
                // 返回图标ResId
                .backResId(R.drawable.ic_arrow_back)
                .title("图片")
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(1)
                .build();
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            imagePath = pathList.get(0);
            ImageLoaderUtils.display(getActivity(), ivAddPic, imagePath);
            ivDelete.setVisibility(View.VISIBLE);
        }
    }
}
