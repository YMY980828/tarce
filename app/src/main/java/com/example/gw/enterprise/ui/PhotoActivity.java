package com.example.gw.enterprise.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gw.enterprise.R;
import com.example.gw.enterprise.adapter.NinePicturesAdapter;
import com.example.gw.enterprise.common.base.BaseActivity;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.component.NoScrollGridView;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.utils.ImageLoaderUtils;
import com.example.gw.enterprise.photoPicker.ImageLoader;
import com.example.gw.enterprise.photoPicker.ImgSelActivity;
import com.example.gw.enterprise.photoPicker.ImgSelConfig;
import com.example.gw.enterprise.task.Task_GetUploadToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by gw on 2018/8/27.
 */

public class PhotoActivity extends BaseActivity {
    @InjectView(R.id.gridview)
    NoScrollGridView gridview;
    @InjectView(R.id.tv_save)
    TextView tvSave;
    private NinePicturesAdapter ninePicturesAdapter;
    private int REQUEST_CODE = 120;
    private List<String> uploadPaths = new ArrayList<>();
    private String imgPaths, imgUrl;
    private int picNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.act_publish_zone);
        getNbBar().setNBTitle("拍照");
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                new TaskThread().start();
            }
        });
        ninePicturesAdapter = new NinePicturesAdapter(this, 9, new NinePicturesAdapter.OnClickAddListener() {
            @Override
            public void onClickAdd(int positin) {
                choosePhoto();
            }
        });
        gridview.setAdapter(ninePicturesAdapter);
    }

    private void getUploadToken() {
        Task_GetUploadToken task = new Task_GetUploadToken();
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {
                    JsonObject jsonObj = new JsonParser().parse(obj.toString()).getAsJsonObject();
                    String token = jsonObj.get("data").getAsString();
                    imgUrl = jsonObj.get("data2").getAsString();
                    uploadFile(token, uploadPaths.get(0), uploadPaths.size());
                }
            }
        };
        task.start();
    }

    private void uploadFile(final String token, String picUrl, final int picSize) {
        showLoading();
        UploadManager uploadManager = new UploadManager();
        // 设置图片名字
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String key = "icon_" + sdf.format(new Date()) + "-" + Math.round(Math.random() * 1000) + ".jpg";
        uploadManager.put(picUrl, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                // 上传成功后将key值上传到自己的服务器
                if (info.isOK()) {
                    if (picNum == 0) {
                        imgPaths = imgUrl + "/" + key;
                    } else {
                        imgPaths = imgPaths + ";" + imgUrl + "/" + key;
                    }
                    picNum++;
                    if (picNum < picSize) {
                        uploadFile(token, uploadPaths.get(picNum), picSize);
                    } else {
                        hideLoading();
                        picNum = 0;
                        Intent intent = new Intent();
                        intent.putExtra("filePaths", imgPaths);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        }, null);
    }

    /**
     * 开启图片选择器
     */
    private void choosePhoto() {
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
                .maxNum(3 - ninePicturesAdapter.getPhotoCount())
                .build();
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            ImageLoaderUtils.display(context, imageView, path);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            if (ninePicturesAdapter != null) {
                ninePicturesAdapter.addAll(pathList);
            }
        }
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
        uploadPaths.clear();
        List<String> pic = ninePicturesAdapter.getData();
        int size = pic.size();
        String path, uploadPath;
        for (int i = 0; i < size; i++) {
            // 压缩保存
            if (!TextUtils.isEmpty(pic.get(i))) {
                path = pic.get(i);
                uploadPath = compressPath(path);
                uploadPaths.add(uploadPath);
            }
        }
    }

}
