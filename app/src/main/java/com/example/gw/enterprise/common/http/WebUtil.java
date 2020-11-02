package com.example.gw.enterprise.common.http;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by gw on 15/6/18.
 */
public class WebUtil {
    public static String doPost(String weburl, RequestBody body) {
        /**
         *创建请求的参数body
         */
        String result = "";
//        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(FrmApplication.applicationContext));

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .cookieJar(cookieJar)
//                .build();
//        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();
        Request request = new Request.Builder()
                .url(weburl)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 上传文件及参数
     */
    public static String postfile(String baseurl, List<File> fileList) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        String result = "";
        OkHttpClient mOkHttpClient = getUnsafeOkHttpClient();
        MultipartBody.Builder mbody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (File file : fileList) {
            String filename;
            filename = "partFile";
            if (file.exists()) {
                Log.i("imageName:", file.getName());//经过测试，此处的名称不能相同，如果相同，只能保存最后一个图片，不知道那些同名的大神是怎么成功保存图片的。
                mbody.addFormDataPart(filename, file.getName(), RequestBody.create(MEDIA_TYPE_MARKDOWN, file));
            }
        }

        RequestBody requestBody = mbody.build();
        Request request = new Request.Builder()
                .url(baseurl)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            result = response.body().string();
            Log.i("InfoMSG", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
