package com.example.gw.enterprise.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gw.enterprise.R;

/**
 * Created by gw on 2018/8/30.
 */

public class DialogUtil {
    public static void showErrorDialog(final Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(
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
            }
        });
        Button btnQx = (Button) view.findViewById(R.id.negativeButton);
        btnQx.setVisibility(View.GONE);
        alertDialog.show();
    }
}
