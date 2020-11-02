package com.example.gw.enterprise.common;

import android.content.Context;
import android.provider.MediaStore;

import com.example.gw.enterprise.common.utils.AppUtil;

import java.io.File;

/**
 * Created by gw on 2018/8/28.
 */

public class FileConfig {
    public static void createFolers(String[] folders) {

        for (String folder : folders) {
            File f = new File(AppUtil.getStoragePath() + "/" + folder);

            if (!f.exists()) {
                f.mkdirs();
            }
        }
    }

    public static void initFolders() {
        createFolers(new String[]{"attach", "update", "upload"});
    }

    public static void clearUpload(Context context) {
        File f = new File(AppUtil.getStoragePath() + "/upload");
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
            String params[] = new String[]{file.getPath()};
            context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , MediaStore.Audio.Media.DATA + " LIKE ?", params);
        }
    }
}
