package com.example.gw.enterprise.common.controls;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gw on 15/4/16.
 */
public class FrmTabbar {

    private LinearLayout llTabbar;
    private FrmTabIconModel[] tabModels;
    public ArrayList<FrmTabItemViewModel> itemsList = new ArrayList<>();

    private int normalColor = Color.GRAY;
    private int selectedColor = Color.GRAY;

    private FrmTabbarListener tabbarListener;

    public FrmTabbar(Activity context, FrmTabIconModel[] tabModels) {
        this.tabModels = tabModels;
        llTabbar = (LinearLayout) context.findViewById(ResManager.getIdInt("llTabbar"));
    }

    public FrmTabbar(View v, FrmTabIconModel[] tabModels) {
        this.tabModels = tabModels;
        llTabbar = (LinearLayout) v;
    }

    public void create() {
        for (int i = 0; i < llTabbar.getChildCount(); i++) {
            final int index = i;
            RelativeLayout item = (RelativeLayout) llTabbar.getChildAt(i);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabbarItemClick(index);
                }
            });
            FrmTabItemViewModel viewModel = new FrmTabItemViewModel();

            for (int j = 0; j < item.getChildCount(); j++) {
                View v = item.getChildAt(j);

                if (v instanceof ImageView) {
                    viewModel.ivIcon = (ImageView) v;
                    viewModel.ivIcon.setColorFilter(normalColor);
                } else if (v instanceof TextView) {
                    viewModel.tvTitle = (TextView) v;
                    viewModel.tvTitle.setTextColor(normalColor);
                } else if (v instanceof RelativeLayout) {
                    RelativeLayout rlTipsArea = (RelativeLayout) v;
                    viewModel.tvTips = (TextView) rlTipsArea.getChildAt(0);
                }
            }
            FrmTabIconModel frmTabIconModel = tabModels[i];
            viewModel.tvTitle.setText(frmTabIconModel.title);
            viewModel.ivIcon.setImageResource(frmTabIconModel.normalIcon);
            itemsList.add(viewModel);
        }
    }

    public void setNormalColor(int color) {
        normalColor = color;
    }

    public void setSelectedColor(int color) {
        selectedColor = color;
    }

    public void tabbarItemClick(int index) {
        changeSelectedIcon(index);
        if (tabbarListener != null) {
            this.tabbarListener.tabbarItemClickListener(index);
        }
    }

    public void changeSelectedIcon(int index) {
        for (int i = 0; i < llTabbar.getChildCount(); i++) {
            FrmTabIconModel frmTabIconModel = tabModels[i];
            itemsList.get(i).ivIcon.setImageResource(frmTabIconModel.normalIcon);
            itemsList.get(i).ivIcon.setColorFilter(normalColor);
            itemsList.get(i).tvTitle.setTextColor(normalColor);
        }
        itemsList.get(index).ivIcon.setImageResource(tabModels[index].selectedIcon);
        itemsList.get(index).ivIcon.setColorFilter(selectedColor);
        itemsList.get(index).tvTitle.setTextColor(selectedColor);
    }

    public void setItemTipsValue(int index, String tips) {
        try {
            if (Integer.valueOf(tips) > 99) {
                tips = "99";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (tips == null || tips.trim().length() == 0 || "0".equals(tips.trim())) {
            itemsList.get(index).tvTips.setVisibility(View.GONE);
        } else {
            itemsList.get(index).tvTips.setVisibility(View.VISIBLE);
            itemsList.get(index).tvTips.setText(tips);
        }
    }

    public void setTabbarListener(FrmTabbarListener tabbarListener) {
        this.tabbarListener = tabbarListener;
    }

    public interface FrmTabbarListener {

        void tabbarItemClickListener(int index);

    }
}
