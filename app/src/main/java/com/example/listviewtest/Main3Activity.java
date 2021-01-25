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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Main3Activity extends AppCompatActivity {

    private  TextView mName,mNum,mInstall,mOder,weight,Material;
    private Button mDoIt,query;
    private ListView mList;
    private List<Data>mData;
    private MyAdapter myAdapter = null;
    private Context context = null;
    private String myurl = "";//产品信息请求地址
    private int i = 0;
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
        Material = findViewById(R.id.MaterialNum);//材料号

        myAdapter = new MyAdapter((LinkedList<Data>)mData,context);
        mList.setAdapter(myAdapter);
        final Intent intent = getIntent();
        Bundle bd =intent.getExtras();
        assert bd != null;
        String model = bd.getString("model");
        String num = bd.getString("num");
        mName.setText(model);
        mNum.setText(num);
        //提交装车实绩
        mDoIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int wt = 0;
                        for (int i=0;i<myAdapter.getCount();i++){
                            LinkedList<Data>mData2 = (LinkedList<Data>)myAdapter.getItem(i);
                            System.out.println("msg3-----------" + mData2.get(0).getWeight());
                            String wt1 = mData2.get(0).getWeight();
                            wt = Integer.parseInt(wt1)+wt;
                        }
                        JSONObject obj = new JSONObject();
                        try {
                            //提交装车实绩
                            obj.put("code","");//出库编码
                            obj.put("methodType",1);//出库方式
                            obj.put("batchCode","");//出库批次
                            obj.put("userName","");//操作员
                            obj.put("car_no","");//车号
                            obj.put("userCode","");//操作员编码
                            obj.put("consigne_user_name","");//收货客户
                            obj.put("weight",wt);//重量
                            obj.put("consigne_addr_name","");//收获地址
                            obj.put("number",50);//出库数量
                            obj.put("goodsCode","");//物料编码
                            obj.put("breanchNum",213);//出库支数
                            obj.put("id",1223);//默认主键
                            obj.put("goodsName","");//物料名称
                            obj.put("createtime","");//出库时间
                            obj.put("specsname","");//物料规格
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
                    }
                }).start();
            }
        });
        if (mNum.getText()==mInstall.getText()){
            Toast.makeText(Main3Activity.this,"以达到订单数量",Toast.LENGTH_SHORT).show();
        }

        
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
                final String message = intent.getStringExtra("scannerdata");//材料号

                //Material.setText(message);
                JSONObject object = new JSONObject();
                final String[] msgs = {null};
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            msgs[0] = http_Get.getData(dataURL+message);//材料号请求产品信息
                            if (msgs[0]==null){
                                runOnUiThread();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(msgs[0]);
                            JSONObject object1 = jsonObject.getJSONObject("response");
                            final String goodsName = object1.getString("goodsName");
                            final String specs = object1.getString("specs");
                            final String weight = object1.getString("weight");
                            final int branchNum = object1.getInt("branchNum");
                            final String meteringCode = object1.getString("meteringCode");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myAdapter.add(new Data
                                            (goodsName+"&"+specs,weight,String.valueOf(branchNum),message));//扫码显示产品信息
                                    Material.setText(meteringCode);
                                    /*int oldNum = Integer.getInteger(mInstall.getText().toString());
                                    String nowNum = String.valueOf(oldNum+branchNum);
                                    mInstall.setText(nowNum);//显示已装数量*/
                                }
                            });

                        } catch (JSONException e) {
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
