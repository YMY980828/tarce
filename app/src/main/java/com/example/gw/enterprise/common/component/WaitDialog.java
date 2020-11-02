package com.example.gw.enterprise.common.component;

import android.app.Dialog;
import android.content.Context;

import com.example.gw.enterprise.R;


/**
 * 自定义加载对话框
 * Created by gw on 15/9/21.
 */
public class WaitDialog extends Dialog {


    public WaitDialog(Context context, int themeResId) {
        super(context, themeResId);
        /**设置对话框背景透明*/
        setContentView(R.layout.dialog_transparent);
        setCanceledOnTouchOutside(false);
    }
}