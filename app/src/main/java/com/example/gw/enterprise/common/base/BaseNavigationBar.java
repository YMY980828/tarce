package com.example.gw.enterprise.common.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gw.enterprise.R;


/**
 * Created by gw on 17/5/14.
 * 基本导航栏
 */
public class BaseNavigationBar {

    public ImageView nbBack;

    public ImageView nbRight;

    public TextView nbRightText;

    public TextView nbTitle;

    public RelativeLayout root;

    public BaseNavigationBar(View rootView, final NBBarAction action) {
        nbBack = (ImageView) rootView.findViewById(R.id.nbBack);
        nbRight = (ImageView) rootView.findViewById(R.id.nbRight);
        nbRightText = (TextView) rootView.findViewById(R.id.nbRightText);
        nbTitle = (TextView) rootView.findViewById(R.id.nbTitle);
        root = (RelativeLayout) rootView.findViewById(R.id.nbRoot);
        nbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.onNBBack();
            }
        });

        nbRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.onNBRight();
            }
        });

        nbRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.onNBRight();
            }
        });

    }

    public void hide() {
        root.setVisibility(View.GONE);
    }

    public void show() {
        root.setVisibility(View.VISIBLE);
    }

    public void setNbBackImage(int resid) {
        nbBack.setImageResource(resid);
    }

    /**
     * 设置NB背景图
     *
     * @param resid
     */
    public void setNBBackground(int resid) {
        if (root != null) {
            root.setBackgroundResource(resid);
        }
    }

    public void setNBTitle(CharSequence title) {
        nbTitle.setVisibility(View.VISIBLE);
        nbTitle.setText(title);
    }

    public interface NBBarAction {

        void onNBBack();

        void onNBRight();

    }

}
