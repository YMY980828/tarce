package com.example.gw.enterprise.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.dascom.print.DeviceListActivity;
import com.dascom.print.SmartPrint;
import com.example.gw.enterprise.BuildConfig;
import com.example.gw.enterprise.R;
import com.example.gw.enterprise.common.FileConfig;
import com.example.gw.enterprise.common.action.UpdateAction;
import com.example.gw.enterprise.common.base.BaseActivity;
import com.example.gw.enterprise.common.base.BaseApplication;
import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.db.DBManager;
import com.example.gw.enterprise.common.db.FrmConfigKeys;
import com.example.gw.enterprise.common.http.CommnAction;
import com.example.gw.enterprise.common.utils.ToastUtil;
import com.example.gw.enterprise.task.Task_SavePrintRecord;
import com.example.gw.enterprise.unknow.Activity_DeviceList4;
import com.example.gw.enterprise.webview.X5WebView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sdk.print.PrintPort;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.UUID;

import HPRTAndroidSDK.ZPL.HPRTPrinterHelper;
import HPRTAndroidSDK.ZPL.PublicFunction;
import butterknife.InjectView;
import hddm.Activity_DeviceList3;
import hprt.Activity_DeviceList;
import hprt.Activity_DeviceList2;
import hprt.checkClick;
import zxing.activity.CaptureActivity;

import static HPRTAndroidSDKA300.HPRTPrinterHelper.LanguageEncode;

/**
 * Created by gw on 2018/8/28.
 */

public class TBSWebViewActivity extends BaseActivity {
    @InjectView(R.id.myWebView)
    X5WebView myWebView;
    //汉印蓝牙打印
    private Context thisCon = null;
    private BluetoothAdapter mBluetoothAdapter;
    private PublicFunction PFun = null;
    private String ConnectType = "";
    private HPRTPrinterHelper hprtPrinterHelper;
    //得实蓝牙打印
    private final int REQUEST_CONNECT_DEVICE = 1;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    private BluetoothAdapter mBluetoothAdapterDS = null;
    private SmartPrint mSmartPrint = null;
    private boolean btopenflag = false, firstConnect = true;
    String btAddress = "";
    //HDDM
    private PrintPort iPrinter;
    private boolean isConnected = false;
    //传的字段
    private String type, title1, title2, name, user, phone, code, company, qrCode, num, weight, time, address, reverse,productName,productId,traceCode,printKindId,specification,count;
    BluetoothSocket bluetoothSocket=null;
    private String weightText="",unitId="",orgProductGreedId="";
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.tbs_webview_activity);
        getNbBar().hide();
        InitSetting();
        //更新
        UpdateAction.updateAction(this, false);
        /**
         * pda:ver=PDA
         */

        myWebView.loadUrl(BuildConfig.Main_URL + "?token=" + DBManager.getOtherConfig(FrmConfigKeys.token));
        //   myWebView.loadUrl("http://192.168.1.116:9001" + "?token=" + DBManager.getOtherConfig(FrmConfigKeys.token));
        myWebView.addJavascriptInterface(new method(), "method");
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载完成
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //加载开始
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //加载失败
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 　　//cancel(); 默认的处理方式，WebView变成空白页
                handler.proceed(); // 接受证书
            }
        });
    }

    private void InitSetting() {
        thisCon = this.getApplicationContext();
        PFun = new PublicFunction(thisCon);
        String SettingValue = "";
        SettingValue = PFun.ReadSharedPreferencesData("Codepage");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Codepage", "0,PC437(USA:Standard Europe)");
        SettingValue = PFun.ReadSharedPreferencesData("Cut");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Cut", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Cashdrawer");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Cashdrawer", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Buzzer");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Buzzer", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Feeds");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Feeds", "0");
    }

    //EnableBluetooth
    private boolean EnableBluetooth() {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } else {
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString());
        }
        return bRet;
    }

    public class method {
        @JavascriptInterface
        public void back() {
            finish();
        }

        @JavascriptInterface
        public void choosePic() {
            Intent intent = new Intent(getActivity(), PhotoActivity.class);
            startActivityForResult(intent, 1001);
        }

        @JavascriptInterface
        public String getVersion() {
            return UpdateAction.getAppVersion();

        }

        @JavascriptInterface
        public void login() {
            DBManager.setOtherConfig(FrmConfigKeys.token, "");
            DBManager.setOtherConfig(FrmConfigKeys.loginResult, "");
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public String getToken() {
            return DBManager.getOtherConfig(FrmConfigKeys.loginResult);
        }

        @JavascriptInterface
        public void quickMark() {
            if (ContextCompat.checkSelfPermission(TBSWebViewActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(TBSWebViewActivity.this, new String[]{Manifest.permission.CAMERA},
                        1);
            }//这一块红色的是开启电话里的相机权限，安卓6.0以后的系统需要，否则会报错
            Intent intent = new Intent(TBSWebViewActivity.this, CaptureActivity.class);//黄色是第三方类库里面的类
            startActivityForResult(intent, 1002);
        }

        @JavascriptInterface
        public void print(String json) {
           try {
               JsonObject data = new JsonParser().parse(json).getAsJsonObject();
               type = data.get("types").getAsString();
               JsonElement e = data.get("unitId");
               if (e!=null){
                   unitId = e.getAsString();
               }
               JsonElement e1 = data.get("orgProductGreedId");
               if (e1!=null){
                   orgProductGreedId =e1.getAsString();
               }
               JsonElement e2 = data.get("weight");
               if (e2!=null){
                   weightText = e2.getAsString();
               }
               title1 = data.get("title1").getAsString();
               title2 = data.get("title2").getAsString();
               name = data.get("productName").getAsString();
               user = data.get("person").getAsString();
               phone = data.get("phone").getAsString();
               code = data.get("shortCode").getAsString();
               company = data.get("orgName").getAsString();
               qrCode = data.get("codeUrl").getAsString();
               num = data.get("printNum").getAsString();
               weight = data.get("weightText").getAsString();
               time = data.get("time").getAsString();
               address = data.get("place").getAsString();
               reverse = data.get("reverse").getAsString();

               productName = name;
               productId = data.get("productId").getAsString();
               traceCode = data.get("traceCode").getAsString();
               printKindId = data.get("printKindId").getAsString();
               specification = data.get("specification").getAsString();
               count = data.get("count").getAsString();
               //Enable Bluetooth
               EnableBluetooth();
               if (!checkClick.isClickEvent()) return;
               if (HPRTAndroidSDKA300.HPRTPrinterHelper.IsOpened()) {
                   savePrintRecord(1);
               } else {
                   connectBT(1);
               }
           }catch (Exception e){
               e.printStackTrace();
           }

        }

        @JavascriptInterface
        public void printDS(String json) {
            JsonObject data = new JsonParser().parse(json).getAsJsonObject();
            type = data.get("types").getAsString();
            title1 = data.get("title1").getAsString();
            title2 = data.get("title2").getAsString();
            name = data.get("productName").getAsString();
            user = data.get("person").getAsString();
            phone = data.get("phone").getAsString();
            code = data.get("shortCode").getAsString();
            company = data.get("orgName").getAsString();
            qrCode = data.get("codeUrl").getAsString();
            num = data.get("printNum").getAsString();
            weight = data.get("weightText").getAsString();
            time = data.get("time").getAsString();
            address = data.get("place").getAsString();
            reverse = data.get("reverse").getAsString();

            productName = name;
            productId = data.get("productId").getAsString();
            traceCode = data.get("traceCode").getAsString();
            printKindId = data.get("printKindId").getAsString();
            specification = data.get("specification").getAsString();
            count = data.get("count").getAsString();
            //Enable Bluetooth
            if (!checkClick.isClickEvent()) return;
            boolean res = isConnectPrinter();
            if (!res) {
                BTPrint();
                connectToPrint();
                return;
            } else {
                savePrintRecord(5);
              //  DSPrint();
            }
        }

        @JavascriptInterface
        public void printHY2(String json) {
            JsonObject data = new JsonParser().parse(json).getAsJsonObject();
            type = data.get("types").getAsString();
            title1 = data.get("title1").getAsString();
            title2 = data.get("title2").getAsString();
            name = data.get("productName").getAsString();
            user = data.get("person").getAsString();
            phone = data.get("phone").getAsString();
            code = data.get("shortCode").getAsString();
            company = data.get("orgName").getAsString();
            qrCode = data.get("codeUrl").getAsString();
            num = data.get("printNum").getAsString();
            weight = data.get("weightText").getAsString();
            time = data.get("time").getAsString();
            address = data.get("place").getAsString();
            reverse = data.get("reverse").getAsString();

            productName = name;
            productId = data.get("productId").getAsString();
            traceCode = data.get("traceCode").getAsString();
            printKindId = data.get("printKindId").getAsString();
            specification = data.get("specification").getAsString();
            count = data.get("count").getAsString();
            //Enable Bluetooth
            EnableBluetooth();
            hprtPrinterHelper = HPRTPrinterHelper.getHPRT(thisCon);
            if (!checkClick.isClickEvent()) return;
            if (HPRTPrinterHelper.IsOpened()) {
                savePrintRecord(2);
            } else {
                connectBT(2);
            }
        }


        @JavascriptInterface
        public void printHDDM(String json) {
            JsonObject data = new JsonParser().parse(json).getAsJsonObject();
            type = data.get("types").getAsString();
            title1 = data.get("title1").getAsString();
            title2 = data.get("title2").getAsString();
            name = data.get("productName").getAsString();
            user = data.get("person").getAsString();
            phone = data.get("phone").getAsString();
            code = data.get("shortCode").getAsString();
            company = data.get("orgName").getAsString();
            qrCode = data.get("codeUrl").getAsString();
            num = data.get("printNum").getAsString();
            weight = data.get("weightText").getAsString();
            time = data.get("time").getAsString();
            address = data.get("place").getAsString();
            reverse = data.get("reverse").getAsString();

            productName = name;
            productId = data.get("productId").getAsString();
            traceCode = data.get("traceCode").getAsString();
            printKindId = data.get("printKindId").getAsString();
            specification = data.get("specification").getAsString();
            count = data.get("count").getAsString();
            //Enable Bluetooth
            if (!checkClick.isClickEvent()) return;
            if (isConnected) {
                savePrintRecord(3);
            } else {
                connectBT(3);
            }
        }

        //pda
        @JavascriptInterface
        public void printPDA(String json){
            JsonObject data = new JsonParser().parse(json).getAsJsonObject();
            type = data.get("types").getAsString();
            title1 = data.get("title1").getAsString();
            title2 = data.get("title2").getAsString();
            name = data.get("productName").getAsString();
            user = data.get("person").getAsString();
            phone = data.get("phone").getAsString();
            code = data.get("shortCode").getAsString();
            company = data.get("orgName").getAsString();
            qrCode = data.get("codeUrl").getAsString();
            num = data.get("printNum").getAsString();
            weight = data.get("weightText").getAsString();
            time = data.get("time").getAsString();
            address = data.get("place").getAsString();
            if (address.length() > 10) {
                address = address.substring(0, 10) + "...";
            }
            reverse = data.get("reverse").getAsString();
            productName = data.get("productName").getAsString();
            productId = data.get("productId").getAsString();
            traceCode = data.get("traceCode").getAsString();
            printKindId = data.get("printKindId").getAsString();
            specification = data.get("specification").getAsString();
            count = data.get("count").getAsString();

            //Enable Bluetooth
            EnableBluetooth();
            if (!checkClick.isClickEvent()) return;
            if (bluetoothSocket!=null){
                savePrintRecord(4);
            }else {
                connectBT(4);
            }

        }

    }




    private void connectBT(int type) {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(TBSWebViewActivity.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TBSWebViewActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
            } else {
                //具有权限
                ConnectType = "Bluetooth";
                if (type == 1) {
                    Intent serverIntent = new Intent(this, Activity_DeviceList.class);
                    startActivityForResult(serverIntent, 1003);
                } else if (type == 2) {
                    Intent serverIntent = new Intent(this, Activity_DeviceList2.class);
                    startActivityForResult(serverIntent, 1004);
                } else if (type == 3) {
                    iPrinter = new PrintPort();
                    Intent serverIntent = new Intent(this, Activity_DeviceList3.class);
                    startActivityForResult(serverIntent, 1005);
                }else if (type==4){

                    Intent serverIntent = new Intent(this, Activity_DeviceList4.class);
                    startActivityForResult(serverIntent, 1006);
                }
            }
        } else {
            //系统不高于6.0直接执行
            ConnectType = "Bluetooth";
            if (type == 1) {
                Intent serverIntent = new Intent(this, Activity_DeviceList.class);
                startActivityForResult(serverIntent, 1003);
            } else if (type == 2) {
                Intent serverIntent = new Intent(this, Activity_DeviceList2.class);
                startActivityForResult(serverIntent, 1004);
            } else if (type == 3) {
                Intent serverIntent = new Intent(this, Activity_DeviceList3.class);
                startActivityForResult(serverIntent, 1005);
            }else if (type==4){

                Intent serverIntent = new Intent(this, Activity_DeviceList4.class);
                startActivityForResult(serverIntent, 1006);
            }
        }
    }

    public void connectToPrint() {
        if (btopenflag) {
            ToastUtil.showShort("请先断开并连接新的蓝牙机器");
            return;
        } else if (mSmartPrint.DSGetState() != 0 && mSmartPrint.DSGetState() != 4) {
            ToastUtil.showShort("正在连接，请稍候。。。");
            return;
        } else {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
    }
    private void UnKnownPrint(Intent intent)  {
        // mBluetoothAdapter
        // BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

        String title1 = "食用农产品合格证: "+name;
        String title2 = "数量(重量): "+weight;
        String title3 = "产地: "+address;
        String title4 = "联系方式: "+phone;
        String title5 = "开具日期: "+time;
        String title6 = "生产者: "+company;

        Bitmap b = Bitmap.createBitmap(500,220, Bitmap.Config.ARGB_8888);
       Bitmap bitmap =  createQRCodeBitmap(qrCode, 180, 180,"UTF-8","H", "1", Color.BLACK, Color.WHITE);
        Paint paint =new Paint();
        Paint paint1 =new Paint();
        paint.setTextSize(18);
        Canvas canvas = new Canvas(b);
//                canvas.rotate(-180);
//                canvas.translate(-280, -300);
        canvas.drawColor(Color.WHITE);
        canvas.drawText(title1,0,20,paint);
        canvas.drawText(title2,0,45,paint);
        canvas.drawText(title3,0,70,paint);
        canvas.drawText(title4,0,95,paint);
        canvas.drawText(title5,0,120,paint);
        canvas.drawText(title6,0,145,paint);
        canvas.drawText(" ",0,195,paint);


        if (bitmap!=null){
        canvas.drawBitmap(bitmap,320,0,paint1);
        }

        Bitmap b1 = BitmapadjustPhotoRotation(b,180);
        printBitmapTest(b1,intent);

    }

    private void printBitmapTest(final Bitmap bitmap,Intent intent)
    {
        if (bluetoothSocket==null){
            BluetoothDevice device = intent.getParcelableExtra("device_address");

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                bluetoothSocket.connect();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        new Thread(){
            @Override
            public void run() {
                try{
                    byte[] printer_init = ESCUtil.init_printer();
                    byte[] selectChinese = ESCUtil.selectChineseMode();
                    byte[] charCode = ESCUtil.selectCharCodeSystem((byte) 0x01);
                    byte[] align0 = ESCUtil.alignMode((byte) 0);
                    byte[] align1 = ESCUtil.alignMode((byte) 1);
                    byte[] align2 = ESCUtil.alignMode((byte) 2);
                    byte[] leftMargin0 = ESCUtil.printLeftMargin(0);
                    byte[] nextLine = ESCUtil.nextLines(10);
                    byte[] performPrint0 = ESCUtil.performPrintAndFeedPaper((byte) 160);
                    byte[] performPrint1 = ESCUtil.performPrintAndFeedPaper((byte) 20);
                    byte[] performPrint = ESCUtil.performPrintLabel(Integer.parseInt(num));
                    byte[][] cmdBytes = {
                            printer_init, selectChinese, charCode,
                            leftMargin0,
                            align1,
                            BitMapUtil.getBitmapPrintData(bitmap,500,1),
                            performPrint
                    };
//                    byte[][] cmdBytes = {printer_init, selectChinese, charCode,leftMargin0,
//                            align0, BitMapUtil.getBitmapPrintData(bitmap,128,0),
//                            align1, BitMapUtil.getBitmapPrintData(bitmap,128,1),
//                            align2,BitMapUtil.getBitmapPrintData(bitmap,128,32),
//                    };
//                    byte[][] cmdBytes1 ={leftMargin0,
//                            align0, BitMapUtil.getBitmapPrintData(mBitmap1,128,0),
//                            align1, BitMapUtil.getBitmapPrintData(mBitmap1,256,0),
//                            align2,  BitMapUtil.getBitmapPrintData(mBitmap1,320,1),
//                            performPrint
//                    };
                    try {
//                        if((socket == null) || (!socket.isConnected()))
//                        {
//                            socket = BluetoothUtil.getSocket(mBluetoothPrinterDevice);
//                        }
                        byte[] data = ESCUtil.byteMerger(cmdBytes);
                        OutputStream out = bluetoothSocket.getOutputStream();
                        out.write(data, 0, data.length);
//                        out.close();
//                        bluetoothSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private Bitmap  BitmapadjustPhotoRotation(Bitmap bm, final int orientationDegree){

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);

            return bm1;

        } catch (OutOfMemoryError ex) {
        }
        return null;

    }

    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String character_set, String error_correction_level,
                                            String margin, int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void BTPrint() {
        mSmartPrint = new SmartPrint(TBSWebViewActivity.this, mHandler, 1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mSmartPrint.DSOpenBT(this);
        }
    }

    //判断是否连接打印机
    private boolean isConnectPrinter() {
        if (!btopenflag) {
            return false;
        }
        return true;
    }
    private Intent IntentData=null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                String filePaths = data.getStringExtra("filePaths");
                myWebView.loadUrl("javascript:getPhotos('" + filePaths + "')");
            }
        } else if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");//这个绿色的result是在第三方类库里面定义的key
                myWebView.loadUrl(result);
            }
        } else if (requestCode == 1003) {
            try {
                String strIsConnected;
                int result = data.getExtras().getInt("is_connected");
                String sdata = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                String address = sdata.substring(sdata.length() - 17);
                String name = sdata.substring(0, (sdata.length() - 17));
                DBManager.setOtherConfig(FrmConfigKeys.macAddr, address);
                DBManager.setOtherConfig(FrmConfigKeys.deviceName, name);
                if (result == 0) {
                    //连接成功
                    ToastUtil.showShort("连接成功");
                    savePrintRecord(1);
                } else {
                    //连接失败
                    ToastUtil.showShort("连接失败");
                }
            } catch (Exception e) {
                Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
            }
        } else if (requestCode == 1004) {
            try {
                String strIsConnected;
                strIsConnected = data.getExtras().getString("is_connected");
                String sdata = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                String address = sdata.substring(sdata.length() - 17);
                String name = sdata.substring(0, (sdata.length() - 17));
                DBManager.setOtherConfig(FrmConfigKeys.macAddr, address);
                DBManager.setOtherConfig(FrmConfigKeys.deviceName, name);
                if (strIsConnected.equals("NO")) {
                    //连接失败
                    ToastUtil.showShort("连接失败");
                } else {
                    //连接成功
                    ToastUtil.showShort("连接成功");
                    savePrintRecord(2);
                  //  HY2Print();
                }
            } catch (Exception e) {
                Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
            }
        } else if (requestCode == 1005) {
            if (resultCode == Activity.RESULT_OK) {
                if (isConnected & (iPrinter != null)) {
                    iPrinter.disconnect();
                    isConnected = false;
                }
                String sdata = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                String address = sdata.substring(sdata.length() - 17);
                String name = sdata.substring(0, (sdata.length() - 17));

                if (!isConnected) {
                    if (iPrinter.connect(name, address)) {
                        isConnected = true;
                        savePrintRecord(3);
                      //  HDDMPrint();
                    } else {
                        isConnected = false;
                    }

                }
            }
        }  else if (requestCode==1006){

            DBManager.setOtherConfig(FrmConfigKeys.macAddr, "00:AA:11:BB:22:CC");
            DBManager.setOtherConfig(FrmConfigKeys.deviceName, "IposPrinter");

            IntentData=data;
          savePrintRecord(4);

        }else if (requestCode == REQUEST_CONNECT_DEVICE) {
            // When DeviceListActivity returns with a device to connect
            if (resultCode == RESULT_OK) {
                String address = data.getStringExtra("device_address");
                String name = data.getStringExtra("device_name");
                DBManager.setOtherConfig(FrmConfigKeys.macAddr, address);
                DBManager.setOtherConfig(FrmConfigKeys.deviceName, name);
                mSmartPrint.DSLinkBT();
            }
        }
    }

    private void savePrintRecord(final int flag){
        Task_SavePrintRecord task = new Task_SavePrintRecord();
        task.productName=productName;
        task.productId=productId;
        task.traceCode=traceCode;
        task.printKindId=printKindId;
        task.specification=specification;
        task.count=count;
        task.weight=weightText;
        task.orgProductGreedId=orgProductGreedId;
        task.unitId=unitId;
        task.refreshHandler = new BaseRequestor.RefreshHandler() {
            @Override
            public void refresh(Object obj) {
                if (CommnAction.CheckY(obj, getActivity())) {{
                    if (flag==1){
                        HYPrint();
                    }else if (flag == 2) {
                        HY2Print();
                    } else if (flag == 3) {
                        HDDMPrint();
                    }else if (flag==4){
                        UnKnownPrint(IntentData);
                    }else if (flag==5){
                        DSPrint();
                    }

                }}


            }
        };
        task.start();


    }


    private void DSPrint() {
        boolean a = false;
        int length = Integer.parseInt(num);
        for (int i = 0; i < length; i++) {
            mSmartPrint.SetPageLength(40 / 25.4); // 设置页长
            mSmartPrint.Print_HLine(2, 0, 0, 0, 0);//一页开始
            mSmartPrint.DSZPLSetPaperOffset(3);
            mSmartPrint.DSSetDirection(0); //设置打印方向
//                mSmartPrint.DSZPLPrintTextLine(0.6, 0.1, Typeface.DEFAULT_BOLD, false, 32, title1);// 打印文本
            mSmartPrint.DSZPLPrintQRCodeEx(0, 2.05, 0.35, 0.75, qrCode);
            mSmartPrint.PrintText(0, 20, 0.24, 0.45, 1, 1, "商品名称：" + name);// 打印文本
            if (TextUtils.isEmpty(time)) {
                mSmartPrint.PrintText(0, 20, 0.24, 0.62, 1, 1, "追溯码：" + code);// 打印文本
                mSmartPrint.PrintText(0, 20, 0.24, 0.79, 1, 1, "重量：" + weight);// 打印文本
                mSmartPrint.PrintText(0, 20, 0.24, 0.96, 1, 1, "电话：" + phone);// 打印文本
            } else {
                mSmartPrint.PrintText(0, 20, 0.24, 0.62, 1, 1, "重量：" + weight);// 打印文本
                mSmartPrint.PrintText(0, 20, 0.24, 0.79, 1, 1, "日期：" + time);// 打印文本
                mSmartPrint.PrintText(0, 20, 0.24, 0.96, 1, 1, "电话：" + phone);// 打印文本
            }
            if (company.length() < 18) {
                mSmartPrint.PrintText(0, 20, 0.24, 1.13, 1, 1, "单位：" + company);
            } else {
                mSmartPrint.PrintText(0, 20, 0.24, 1.13, 1, 1, "单位：" + company.substring(0, 17));
                mSmartPrint.PrintText(0, 20, 0.24, 1.3, 1, 1, company.substring(17));
            }
            mSmartPrint.Print_HLine(3, 0, 0, 0, 0);//一页结束
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void HYPrint() {
        int x = 40;//60
        int y = 90;//50
        try {
            HPRTAndroidSDKA300.HPRTPrinterHelper.printAreaSize("0", "200", "200", "320", num);
            if (!TextUtils.isEmpty(title1)) {
                int length = 0;
                if (title1.length() < 18) {
                    length = (18 - title1.length()) * 15;
                }
                HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "4", "0", length + "", "25", title1);
            }
            HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "8", "0", x + "", y + "", "商品名称：" + name);
            if (TextUtils.isEmpty(time)) {
                HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "8", "0", x + "", y + 35 + "", "追溯码：" + code);
                HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "8", "0", x + "", y + 70 + "", "重量：" + weight);
                HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "8", "0", x + "", y + 105 + "", "电话：" + phone);
            } else {
                HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "8", "0", x + "", y + 35 + "", "重量：" + weight);
                HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "8", "0", x + "", y + 70 + "", "日期：" + time);
                HPRTAndroidSDKA300.HPRTPrinterHelper.Text(HPRTAndroidSDKA300.HPRTPrinterHelper.TEXT, "8", "0", x + "", y + 105 + "", "电话：" + phone);
            }
            HPRTAndroidSDKA300.HPRTPrinterHelper.AutLine(x + "", y + 140 + "", 460, 8, false, false, "单位：" + company);
            HPRTAndroidSDKA300.HPRTPrinterHelper.PrintQR(HPRTAndroidSDKA300.HPRTPrinterHelper.BARCODE, 420 + "", y + "", "2", "3", qrCode);
            HPRTAndroidSDKA300.HPRTPrinterHelper.Form();
            if ("1".equals(reverse)) {
                HPRTAndroidSDKA300.HPRTPrinterHelper.Print();
            } else {
                HPRTAndroidSDKA300.HPRTPrinterHelper.PoPrint();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickWIFI ")).append(e.getMessage()).toString());
        }
    }

    private void HY2Print() {
        try {
            hprtPrinterHelper.start();
            hprtPrinterHelper.WriteData(("^PQ" + num + "," + 0 + "," + 0 + "," + "N" + "\r\n").getBytes(LanguageEncode));
            hprtPrinterHelper.printData("^CI14\r\n");
            hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 90 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "商品名称：" + name + "^FS\r\n").getBytes(LanguageEncode));
            if (TextUtils.isEmpty(time)) {
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 125 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "追溯码：" + code + "^FS\r\n").getBytes(LanguageEncode));
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 160 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "重量：" + weight + "^FS\r\n").getBytes(LanguageEncode));
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 195 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "电话：" + phone + "^FS\r\n").getBytes(LanguageEncode));
            } else {
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 125 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "重量：" + weight + "^FS\r\n").getBytes(LanguageEncode));
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 160 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "日期：" + time + "^FS\r\n").getBytes(LanguageEncode));
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 195 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "电话：" + phone + "^FS\r\n").getBytes(LanguageEncode));
            }
            if (company.length() < 16) {
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 230 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "单位：" + company + "^FS\r\n").getBytes(LanguageEncode));
            } else {
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 230 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + "单位：" + company.substring(0, 16) + "^FS\r\n").getBytes(LanguageEncode));
                hprtPrinterHelper.WriteData(("^FO" + 165 + "," + 265 + "^A" + "@" + "N" + "," + 25 + "," + 25 + "^FD" + company.substring(16) + "^FS\r\n").getBytes(LanguageEncode));
            }
            hprtPrinterHelper.WriteData(("^FO" + 550 + "," + 80 + "\r\n^BQ" + "N" + "," + "2" + "," + "3" + "\r\n^FDQA," + qrCode + "^FS\r\n").getBytes(LanguageEncode));
            hprtPrinterHelper.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void HDDMPrint() {
        int x = 40;//60
        int y = 120;//50
        int size = Integer.parseInt(num);
        for (int i = 0; i < size; i++) {
            iPrinter.pageSetup(560, 340);
            if (!TextUtils.isEmpty(title1)) {
                int length = 0;
                if (title1.length() < 18) {
                    length = (18 - title1.length()) * 15;
                }
                iPrinter.drawText(length, 25, title1, 2, 0, 0, false, false);
            }
            iPrinter.drawText(x, y, "商品名称：" + name, 2, 0, 0, false, false);
            if (TextUtils.isEmpty(time)) {
                iPrinter.drawText(x, y + 35, "追溯码：" + code, 2, 0, 0, false, false);
                iPrinter.drawText(x, y + 70, "重量：" + weight, 2, 0, 0, false, false);
                iPrinter.drawText(x, y + 105, "电话：" + phone, 2, 0, 0, false, false);
            } else {
                iPrinter.drawText(x, y + 35, "重量：" + weight, 2, 0, 0, false, false);
                iPrinter.drawText(x, y + 70, "日期：" + time, 2, 0, 0, false, false);
                iPrinter.drawText(x, y + 105, "电话：" + phone, 2, 0, 0, false, false);
            }
            iPrinter.drawText(x, y + 140, "单位：" + company, 2, 0, 0, false, false);
            iPrinter.drawQrCode(415, y, qrCode, 0, 3, 1);
            if ("1".equals(reverse)) {
                iPrinter.print(0, 0);
            } else {
                iPrinter.print(1, 0);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (myWebView.canGoBack()) {
                myWebView.goBack();// 返回前一个页面
            } else {
                ExitApp();
            }
        }
        return false;
    }

    private long exitTime = 0;

    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showShort("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            FileConfig.clearUpload(BaseApplication.getAppContext());
            finish();
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case STATE_CONNECTED:
                            btopenflag = true;
                            btAddress = mSmartPrint.DSGetBTAddress();
                            if (firstConnect) {
                                firstConnect = false;
                                savePrintRecord(5);
                               // DSPrint();
                            }
                            break;
                        case STATE_CONNECTING:
                            break;
                        case STATE_LISTEN:
                        case STATE_NONE:
                            break;
                        case 4:
                            btopenflag = false;
                            new PrintSample().start();
                            break;
                        default:
                    }
                    break;
            }
        }
    };
    boolean whenDestroy = false;

    class PrintSample extends Thread {
        public void run() {
            while ((!btopenflag) && (!whenDestroy)) {
                if (mSmartPrint.DSGetState() == 0 || mSmartPrint.DSGetState() == 4) {
                    mSmartPrint.DSLinkBT(btAddress);
                    Message msg = mHandler.obtainMessage(6);
                    Bundle bundle = new Bundle();
                    bundle.putString("local", "正在尝试重新连接。。。");
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    System.out.println("正在尝试重新连接。。。");
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (isConnected) {
            iPrinter.disconnect();
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (myWebView != null) {
            myWebView.destroy();
            myWebView.clearCache(true);
        }
        super.onDestroy();
    }
}