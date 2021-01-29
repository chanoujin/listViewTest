package com.example.listviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Main3Activity extends AppCompatActivity {
    public static String consigne_addr_name="";//收货地址
    public static String consigne_user_name="";//收货客户
    public static String userName = "";//操作员
    public static String  vc_no = Main2Activity.cjmsg;//车号
    public String planWeight = "";//计划重量

    private  TextView mName,mNum,mInstall,mOder,mPalanw,mRealw;
    private Button mDoIt,query;
    private ListView mList;
    private List<Data>mData;
    private MyAdapter myAdapter = null;
    private Context context = null;
    private String myurl = "";//产品信息请求地址
    private int i = 0;
    public static String myurls =
            "http://172.15.20.15:8083/Arrange/QueryPageArrange?page=1&intPageSize=6";//订单信息请求地址
    private String dataURL ="http://172.15.20.15:8083/Warehousing/QueryWarehousByMeterCode?meterCode=" ;
//http://jzkchaha.vaiwan.com/Warehousing/QueryWarehousByMeterCode?meterCode=

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mDoIt = findViewById(R.id.doIt);
        mList = findViewById(R.id.list_3_1);
        context = Main3Activity.this;
        mData = new LinkedList<Data>();
        mName = findViewById(R.id.name);//产品名
        mNum = findViewById(R.id.num);//订单数量
        mInstall = findViewById(R.id.installedNum);//已装数量
        mOder = findViewById(R.id.oderNnum);//订单号
        mPalanw = findViewById(R.id.plan_w);//计划重量
        mRealw = findViewById(R.id.real_w);//实际重量
        myAdapter = new MyAdapter((LinkedList<Data>)mData,context);
        mList.setAdapter(myAdapter);
        //接收Activite2参数
        final Intent intent = getIntent();
        Bundle bd =intent.getExtras();
        assert bd != null;
        String model = bd.getString("model");
        String num = bd.getString("num");
        String wt = bd.getString("weight");
        String plan_sub_no = bd.getString("plan_sub_no ");
        final String cardid = bd.getString("cardid");
        mName.setText(model);
        mNum.setText(num);
        mPalanw.setText(wt);
        //提交装车实绩
        mDoIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String mymsg = http_Get.getData(myurls+cardid);
                            JSONObject object = new JSONObject(mymsg);
                            JSONObject result = object.getJSONObject("response");
                            System.out.println("一层"+result);
                            final JSONArray mydata = result.getJSONArray("data");
                            System.out.println("二层"+mydata);
                            JSONObject msgs = mydata.getJSONObject(0);
                            int id = msgs.getInt("id");
                            int wt = 0;
                            for (int i=0;i<myAdapter.getCount();i++){
                                LinkedList<Data>mData2 = (LinkedList<Data>)myAdapter.getItem(i);
                                System.out.println("msg3-----------" + mData2.get(0).getWeight());
                                String wt1 = mData2.get(0).getWeight();
                                String num = mData2.get(0).getNum();//数量
                                String specsname = mData2.get(0).getMsgid();//物料规格
                                wt = Integer.parseInt(wt1)+wt;
                                JSONObject obj = new JSONObject();
                                long timeGetTime = System.currentTimeMillis();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                                        ("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                String time = simpleDateFormat.format(timeGetTime);
                                //提交装车实绩
                                try {

                                    obj.put("createtime",time);//创建时间
                                    obj.put("plannum",Integer.parseInt(mNum.getText().toString()));//计划支数
                                    obj.put("devicecode","001");//设备编号
                                    obj.put("planweight",wt);//计划重量
                                    obj.put("id",1);//默认主键
                                    obj.put("realnum",Integer.parseInt(mInstall.getText().toString()));//实际支数
                                    obj.put("last_devicecode","001");//最后操作设备
                                    obj.put("realweight",Integer.parseInt(mRealw.getText().toString()));//实际重量
                                    obj.put("list_time",time);//最后修改时间
                                    obj.put("status",1);//订单状态
                                    obj.put("last_usercode","cj");//最后操作员工
                                    obj.put("usercode",1);//创建员工编号
                                    obj.put("arrangecode",id);//派车计划主键
                                    obj.put("last_username","lili");//最后操作员工姓名
                                    obj.put("username","askjd");//员工姓名
                                    obj.put("code","32");//装车编号
                                    obj.put("meteringcode","5648976d");//生产计划主键
                                    String msg = postHttp.
                                            postdata("http://172.15.20.15:8083/Delivery/SaveDeliveryList",obj.toString());
                                    JSONObject obj1 = new JSONObject(msg);
                                    boolean success = obj1.getBoolean("success");
                                    if (success){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Main3Activity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Main3Activity.this,"上传失败",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    Intent intent1 = new Intent(Main3Activity.this,Main2Activity.class);
                                    intent.putExtra("weight",wt);
                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //出库表
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Main3Activity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        //点击删除以装项目
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Main3Activity.this);
                builder.setTitle("删除");
                builder.setMessage("是否删除？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myAdapter.remove(position);
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }
    //二维码扫描
    private static final String SCANACTION = "com.android.server.scannerservice.broadcast";
    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter intentFilter=new IntentFilter(SCANACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(scanReceiver, intentFilter);
    }
    BroadcastReceiver scanReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((intent.getAction().equals(SCANACTION)))
            {
                final String message1 = intent.getStringExtra("scannerdata");//材料号
                assert message1 != null;
                String[] mm = message1.split("=");
                final String message = mm[1];
                //Material.setText(message);
                JSONObject object = new JSONObject();
                final String[] msgs = {null};
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            msgs[0] = http_Get.getData(dataURL+message);//材料号请求产品信息
                            if (msgs[0]==null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Main3Activity.this,"网络错误",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                try {
                                    JSONObject jsonObject = new JSONObject(msgs[0]);
                                    JSONObject object1 = jsonObject.getJSONObject("response");
                                    final String goodsName = object1.getString("goodsName");
                                    final String specs = object1.getString("specs");
                                    final String weight = object1.getString("weight");
                                    final int branchNum = object1.getInt("branchNum");//数量
                                    final String meteringCode = object1.getString("meteringCode");//材料号
                                    if (Integer.parseInt(mNum.getText().toString())>Integer.parseInt(mInstall.getText().toString())){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                myAdapter.add(new Data
                                                        (weight,goodsName+"&"+specs,message,String.valueOf(branchNum)));//扫码显示产品信息
                                                //重量、型号、材料号、数量
                                    int oldNum = Integer.parseInt(mInstall.getText().toString());
                                    String nowNum = String.valueOf(oldNum+branchNum);
                                    mInstall.setText(nowNum);//显示已装数量
                                            }
                                        });
                                    }
                                    else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Main3Activity.this,"以达到订单数量",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                assert message != null;
                String[] materialNum = message.split("/",1);
                //根据材料号请求产品信息



            }
        }
    };
}
