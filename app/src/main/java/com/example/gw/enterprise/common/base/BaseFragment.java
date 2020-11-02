package com.example.gw.enterprise.common.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.gw.enterprise.R;
import com.example.gw.enterprise.frgs.MainTabbarFragment;


/**
 * Created by gw on 15/6/3.
 * 基础Fragment控制类
 * 负责：控制导航栏、自定义布局文件、状态项等
 */
public class BaseFragment extends Fragment implements BaseNavigationBar.NBBarAction {

    /**
     * 基础导航栏
     */
    private BaseNavigationBar nbBar;

    /**
     * 自定义布局根视图
     */
    private View rootView;

    /**
     * 自定义布局容器
     */
    private FrameLayout baseContent;
    public static MainTabbarFragment tabbarFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frame_base_activity, container, false);
        baseContent = (FrameLayout) rootView.findViewById(R.id.baseContent);
        nbBar = new BaseNavigationBar(rootView, this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initNB();
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
     * 获取rootView
     *
     * @return
     */
    public View getRootView() {
        return rootView;
    }

    /**
     * 根据id获取View，该id必须包含在baseContent里边
     *
     * @param id
     * @return
     */
    public View findViewById(int id) {
        return getRootView().findViewById(id);
    }

    /**
     * 初始化NB
     */
    public void initNB() {

    }

    /**
     * 设置自定义内容区域布局
     *
     * @param layoutId
     */
    public void setLayout(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(layoutId, null);
        setLayout(view);
    }

    /**
     * 设置自定义内容区域布局
     *
     * @param view
     */
    public void setLayout(View view) {
        baseContent.addView(view);
    }

    @Override
    public void onNBBack() {

    }

    @Override
    public void onNBRight() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            onInVisible();
        } else {
            onVisible();
        }
    }

    /**
     * 当前可视
     */
    public void onVisible() {

    }

    /**
     * 当前不可视
     */
    public void onInVisible() {

    }

    public Context getContext() {
        return getActivity();
    }

    public FrameLayout getBaseContent() {
        return baseContent;
    }
}
