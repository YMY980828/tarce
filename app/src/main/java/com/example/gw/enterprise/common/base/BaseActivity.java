package com.example.gw.enterprise.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.example.gw.enterprise.R;
import com.example.gw.enterprise.common.ThemeConfig;
import com.example.gw.enterprise.common.component.WaitDialog;
import com.example.gw.enterprise.common.utils.AppUtil;
import com.example.gw.enterprise.common.utils.PhotoUtils;
import com.example.gw.enterprise.photoPicker.utils.StatusBarCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;

/**
 * Created by gw on 17/3/14.
 * 基础Activity控制类
 * 负责：控制导航栏、自定义布局文件、状态项等
 */
public class BaseActivity extends Activity implements BaseNavigationBar.NBBarAction {

    private BaseNavigationBar nbBar;

    private View rootView;

    private FrameLayout baseContent;
    private WaitDialog progressDialog;
    protected InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
        SetStatusBarColor();
        LayoutInflater inflater = LayoutInflater.from(this);
        rootView = inflater.inflate(R.layout.frame_base_activity, null);
        setContentView(rootView);

        baseContent = (FrameLayout) rootView.findViewById(R.id.baseContent);
        nbBar = new BaseNavigationBar(rootView, this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor(){
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.main_color));
    }
    @Override
    protected void onResume() {
        super.onResume();
        initNB();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void initNB() {
        ThemeConfig.setDefaultViewConfig(getNbBar(), getActivity());
    }


    /**
     * 获取NB导航栏
     *
     * @return
     */
    public BaseNavigationBar getNbBar() {
        return nbBar;
    }

    /**
     * 设置自定义布局
     *
     * @param layoutId
     */
    public void setLayout(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(layoutId, null);
        setLayout(view);
    }

    public void setLayout(View view) {
        baseContent.addView(view);
        //支持注解绑定
        ButterKnife.inject(this);
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public Context getContext() {
        return this;
    }

    public Activity getActivity() {
        return this;
    }

    @Override
    public void onNBBack() {
        finish();
    }

    @Override
    public void onNBRight() {
    }

    /**
     * 获取当前RootView
     *
     * @return
     */
    public View getRootView() {
        return rootView;
    }

    /**
     * 显示加载对话框
     */
    public void showLoading() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = new WaitDialog(getContext(), R.style.DialogStyle);
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    /**
     * 取消加载对话框
     */
    public void hideLoading() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public String compressPath(String path) {
        File appDir = new File(AppUtil.getStoragePath() + "/upload");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String uploadPath = AppUtil.getStoragePath() + "/upload/" + sdf.format(new Date()) + ".jpg";
        Bitmap bm = PhotoUtils.getBitmapFromFile(path, 1200);
        if (bm != null) {
            PhotoUtils.savePhotoToSDCard(bm, uploadPath, 300);
        }
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
        return uploadPath;
    }
}
