package com.example.gw.enterprise.common.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;

import com.example.gw.enterprise.R;
import com.example.gw.enterprise.common.utils.AppUtil;
import com.example.gw.enterprise.ui.TBSWebViewActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    // notification 名字
    private String notify_name = "apk正在下载...";
    public static final String INTENT_URL = "url";
    private Context mContext = this;
    Notification mNotification;
    private static final int NOTIFY_ID = 0;
    private NotificationManager mNotificationManager;
    /* 下载包安装路径 */
    private static final String savePath = AppUtil.getStoragePath() + "/update/";
    private static final String saveFileName = savePath + "zsxt.apk";
    private String apkUrl;
    private int progress;
    boolean canceled;
    private Thread downLoadThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(DownloadService.INTENT_URL)) {
            apkUrl = intent.getStringExtra(DownloadService.INTENT_URL);
        }
        progress = 0;
        setUpNotification();
        new Thread() {
            public void run() {
                // 开始下载
                startDownload();
            }
        }.start();

        return startId;

    }

    ;

    private void startDownload() {
        canceled = false;
        downloadApk();
    }

    private Handler mHandler = new Handler() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 下载完毕
                    // 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    installApk();
                    break;
                case 2:
                    // 这里是用户界面手动取消，所以会经过activity的onDestroy();方法
                    // 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    break;
                case 1:
                    int rate = msg.arg1;
                    if (rate < 100) {
                        RemoteViews contentview = mNotification.contentView;
                        contentview.setTextViewText(R.id.tv_progress, rate + "%");
                        contentview.setProgressBar(R.id.progressbar, 100, rate, false);
                    } else {
                        // 下载完毕后变换通知形式
                        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                        mNotification.contentView = null;
//					mNotification.setLatestEventInfo(mContext, "下载完成", "文件已下载完毕", null);
                        mNotification = new Notification.Builder(mContext)
                                .setContentTitle("文件已下载完毕")
                                .setContentText("下载完成")
                                .setSmallIcon(R.drawable.ic_launcher)
                                .build();
                        stopSelf();// 停掉服务自身
                    }
                    PendingIntent contentIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                    mNotification.contentIntent = contentIntent2;
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    break;
                case 3:
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.update_download_notification_layout);
                    contentView.setTextViewText(R.id.name, "下载失败");
                    // 指定个性化视图
                    mNotification.contentView = contentView;
                    Intent intent = new Intent(getApplicationContext(), TBSWebViewActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // 指定内容意图
                    mNotification.contentIntent = contentIntent;
                    mNotificationManager.notify(NOTIFY_ID, mNotification);

                    stopSelf();// 停掉服务自身
                    break;

            }
        }
    };

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            Uri apkUri = FileProvider.getUriForFile(mContext, getApplication().getPackageName() + ".provider", apkfile); //与manifest中定义的provider中的authorities="com.shawpoo.app.fileprovider"保持一致
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }

    private int lastRate = 0;
    private InputStream is = null;
    private FileOutputStream fos = null;

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = progress;
                    if (progress >= lastRate + 1) {
                        mHandler.sendMessage(msg);
                        lastRate = progress;
                    }
                    if (numread <= 0) {
                        mHandler.sendEmptyMessage(0);// 下载完成通知安装
                        // 下载完了，cancelled也要设置
                        canceled = true;
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!canceled);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (Exception e) {

                Message msg = mHandler.obtainMessage();
                msg.what = 3;
                mHandler.sendMessage(msg);
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    is.close();
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    /**
     * 创建通知
     */
    private void setUpNotification() {
        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "channel_001";
            String name = "name";
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
            notificationBuilder = new Notification.Builder(mContext, id);
        } else {
            notificationBuilder = new Notification.Builder(mContext);
        }
        notificationBuilder.setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setContentTitle("开始下载")
                .setSmallIcon(R.drawable.ic_launcher);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        mNotification = notificationBuilder.build();
//        } else {
//            mNotification = notificationBuilder.getNotification();
//        }

//        int icon = R.drawable.ic_launcher;
//        CharSequence tickerText = "开始下载";
//        long when = System.currentTimeMillis();
//        mNotification = new Notification(icon, tickerText, when);
        // 放置在"正在运行"栏目中
//        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.update_download_notification_layout);
        contentView.setTextViewText(R.id.name, notify_name);
        // 指定个性化视图
        mNotification.contentView = contentView;

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

}
