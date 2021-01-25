package com.example.listviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class logIn extends AppCompatActivity {
    private Context mContext;
    public static  String LOGIN_URL = "http://172.15.20.15:8083/Login/NewLogin";
    private TextView tv_pro;
    private  PackageInfo packageInfo;
    private DownloadManager downloadManager;
    private BroadcastReceiver receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initView();
        mContext = logIn.this;
        final EditText myID = (EditText)findViewById(R.id.myId);
        final EditText passW = (EditText)findViewById(R.id.mPassword);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str = myID.getText().toString();
                final String str1 = passW.getText().toString();
                //工号和密码不能未空
                if (str.equals("") ||str1.equals("")){
                    Toast.makeText(logIn.this,"工号和密码不能未空",Toast.LENGTH_LONG).show();
                }
                else {//账户密码正确
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject object = new JSONObject();
                            try {
                                object.put("userCode", str);
                                object.put("userPwd", str1);
                                object.put("device_number", "string");
                                String msg = postHttp.postdata(LOGIN_URL,object.toString());
                                JSONObject jsonObject = new JSONObject(JSONTokener(msg));
                                boolean success = jsonObject.getBoolean("success");
                                if(success){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext,"Welcome",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(logIn.this,Main5Activity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext,"密码或账号输入错误",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getJSON();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }
    public void initView(){
        tv_pro = findViewById(R.id.tv_pro);
        tv_pro.setText("版本号：" + getAppVersion());
    }
//获取版本号
    private int getAppVersion() {
        PackageManager pm = getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(getPackageName(),0);
            String name = packageInfo.versionName;
            int version = packageInfo.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //比较版本号
    private void getJSON() throws IOException {
        String version = http_Get.getData("http://172.15.20.15:8080/PIMS/output.json");
        try {
            JSONObject jsonObject = new JSONObject(version);
            String versionName = jsonObject.getString("VersionName");
            int versionCode = jsonObject.getInt("versionCode");
            String content = jsonObject.getString("content");
            if (versionCode>getAppVersion()){
                final AlertDialog.Builder builder = new AlertDialog.Builder(logIn.this);
                builder.setTitle("检测到软件更新");
                builder.setMessage("是否更新？");
                builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DownloadUtils(logIn.this,
                                "http://172.15.20.15:8080/PIMS/app-debug.apk",
                                "app-debug.apk");
                    }
                });
                builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //
    /*IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    BroadcastReceiver receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                Receive(intent);
                openFile(new File("/sdcard/Download/time2plato.apk"));

            }else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){
                Toast.makeText(getApplication(),"正在下载",Toast.LENGTH_SHORT).show();
            }
        }


    };


    //跳转更新文件
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivity(intent);
    }
    //APK文件下载
    private void downloadAPK(){
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse("http://172.15.20.15:8080/app-debug.apk"));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("下载");
        request.setDescription("PIMS正在下载......");
        //设置漫游状态可以下载
        request.setAllowedOverRoaming(true);
        //设置文件存放目录
        request.setDestinationInExternalFilesDir(this,
                Environment.DIRECTORY_DOWNLOADS,"update.apk");
        //获取系统服务
        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/vnd.android.package-archive");
        long id = downloadManager.enqueue(request);

    }
    //获取文件下载路径
    private void Receive(Intent intent){
        long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
        DownloadManager.Query mydown = new DownloadManager.Query();
        mydown.setFilterById(reference);
        Cursor myDownload = downloadManager.query(mydown);
        if (myDownload.moveToFirst()){
            int fileNameIdx = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            int fileUriIdx = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String fileName = myDownload.getString(fileNameIdx);
            String fileUri = myDownload.getString(fileUriIdx);
        }
        myDownload.close();
    }
    @Override
    protected void onDestroy(){
        if (receiver!=null){
            unregisterReceiver(receiver);
            receiver = null;

        }
        super.onDestroy();
    }*/
}

