package com.example.gw.enterprise.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gw on 2018/8/28.
 */

public class PhotoUtils {
    /**
     * 获取缩略图
     *
     * @param path  图片的路径
     * @param width 设置图片的宽/高不超过该值,如果为0则返回原图
     * @return
     */
    public static Bitmap getBitmapFromFile(String path, int width) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        int be = 1;
        if (width != 0) {
            if (w > h) {
                be = (int) Math.ceil(w * 1.0 / width);
                newOpts.outWidth = width;
                newOpts.outHeight = h * width / w;
            } else {
                be = (int) Math.ceil(h * 1.0 / width);
                newOpts.outHeight = width;
                newOpts.outWidth = w * width / h;
            }
            if (be <= 0) {
                be = 1;
            }
            if (w == 0) {
                return null;
            }
            newOpts.inSampleSize = be;// 设置采样率
            newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            newOpts.inPurgeable = true;// 同时设置才会有效
            newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
            newOpts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(path, newOpts);
        }
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * @param src
     * @param degree
     * @return
     */
    public static Bitmap degreeBitmap(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree); /*翻转90度*/
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap resultBitmap = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        if (resultBitmap != src) {
            src.recycle();
        }
        return resultBitmap;
    }

    /**
     * 按照路径和大小保存图片
     *
     * @param bitmap
     * @param path
     * @param size
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void savePhotoToSDCard(Bitmap bitmap, String path, int size) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            quality -= 10;// 每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
                    fileOutputStream);
        } catch (FileNotFoundException e1) {

        } finally {
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                    bitmap.recycle();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void rotaingImageView(String path) {
        int degree = PhotoUtils.readPictureDegree(path);
        if (degree != 0) {
            BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
            opts.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
            bitmap = PhotoUtils.degreeBitmap(bitmap, degree);
            File file = new File(path);
            FileOutputStream fileOutputStream = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        fileOutputStream);
            } catch (FileNotFoundException e1) {

            } finally {
                if (fileOutputStream != null)
                    try {
                        fileOutputStream.close();
                        bitmap.recycle();
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public static Uri geturi(Context context, android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }
}
