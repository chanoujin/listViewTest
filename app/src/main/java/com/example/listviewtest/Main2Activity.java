package com.example.listviewtest;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.widget.Toast.makeText;
public class Main2Activity extends AppCompatActivity{
    public static String cjmsg = "asdlasd";
    private ListView listtwo;
    private MyAdapter myAdapter = null;
    private MyAdapter2 myAdapter2 = null;
    private List<Data> mData = null;
    private List<Data2> mData2 = null;
    private Context mContextt = null;
    private TextView mCorporateName,mCarNum,mPlanWt,mRealWt;
    private Button button,button1,found;
    private EditText mCardId;
    private NfcAdapter mNfcAdapter;
    private PendingIntent pi;
    private ArrayAdapter adapter;
    private String msg = "";
    private String num="";
    //更改项目状态
    public static String myurl =
            "http://172.15.20.15:8083/Arrange/QueryPageArrange?page=1&intPageSize=6";//订单信息请求地址
    public String myurl1 = "";//装车信息上传地址
    private int endWeight = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mCarNum = findViewById(R.id.carNum);
        mPlanWt = findViewById(R.id.planWt);
        mRealWt = findViewById(R.id.realWt);
        mCardId = findViewById(R.id.CardId);
        found = findViewById(R.id.found);
        //点击按卡号查询
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mCarId = mCardId.getText().toString();
                final String myurls = "&cardCode="+mCarId;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //根据卡号拉取装车计划
                            String msgs = http_Get.getData(myurl+myurls);
                            if (msgs==null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Main2Activity.this,"无法连接服务器",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                JSONObject object = new JSONObject(msgs);
                                JSONObject result = object.getJSONObject("response");
                                System.out.println("一层"+result);
                                final JSONArray mydata = result.getJSONArray("data");
                                System.out.println("二层"+mydata);

                                if (mydata.isNull(0)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Main2Activity.this,"此车暂无计划",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (int i=0;i<mydata.length();i++){

                                                try {
                                                    final JSONObject msg = mydata.getJSONObject(i);
                                                    final String car_num = msg.getString("vc_no");//车牌号
                                                    mCarNum.setText(car_num);
                                                    String prod_cname = msg.getString("prod_cname");
                                                    String spec_desc = msg.getString("spec_desc");//产品型号
                                                    String plan_sub_no = msg.getString("plan_sub_no");//登记单号
                                                    int num = msg.getInt("num");//计划数量
                                                    int weight = msg.getInt("wt");//计划重量
                                                    myAdapter2.add(new Data2(spec_desc, String.valueOf(num),
                                                            String.valueOf(weight),plan_sub_no));
                                                    endWeight =weight + endWeight;
                                                    mPlanWt.setText(String.valueOf(endWeight));//计划总重量
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        button = (Button)findViewById(R.id.button2);
        NfcBeg();
        mContextt = Main2Activity.this;
        bindViews();
        mData2 = new LinkedList<Data2>();
        myAdapter2 = new MyAdapter2((LinkedList<Data2>)mData2,mContextt);
        listtwo.setAdapter(myAdapter2);
        Utility.setListViewHeightBasedOnChildren(listtwo);
        //点击订单列表，获取订单信息，跳转至扫码出库界面
        listtwo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinkedList<Data2>mDATA2 = (LinkedList<Data2>) myAdapter2.getItem(position);
                String model = mDATA2.get(0).getmModel();//产品型号
                num = mDATA2.get(0).getmNum();//数量
                String weight = mDATA2.get(0).getmPlanw();//重量
                String plan_sub_no = mDATA2.get(0).mPlan_sub_no;//单号
                Intent intent = new Intent(Main2Activity.this,Main3Activity.class);
                Bundle bd = new Bundle();
                bd.putString("model",model);
                bd.putString("num",num);
                bd.putString("plan_sub_no",plan_sub_no);
                bd.putString("weight",weight);
                bd.putString("cardid",mCardId.getText().toString());
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
        //Utility.setListViewHeightBasedOnChildren(listtwo);
        Intent intent = getIntent();
        String wt = intent.getStringExtra("weight");
        mRealWt.setText(wt);
        //列表点击删除
        /*listone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
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
        });*/
        //点击上传出库按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCarNum.getText().toString()==null){
                    Toast.makeText(Main2Activity.this,"请刷卡",Toast.LENGTH_SHORT).show();
                }

                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /*JSONObject obj = new JSONObject();
                            try {
                                obj.put("car_no",vc_no);//车号
                                obj.put("id",12);//默认主键
                                obj.put("code","");//出库编码
                                obj.put("methodype",1);//出库方式
                                obj.put("consigne_addr_name",consigne_addr_name);//收货地址
                                obj.put("number",Integer.parseInt(num));//出库数量
                                obj.put("consigne_user_name",consigne_user_name);//收货客户
                                obj.put("specsname",specsname);//物料规格
                                obj.put("createtime",time);//出库时间
                                obj.put("userCode","01");//操作员编码
                                obj.put("entruckingCode","01");//装车编码
                                obj.put("userName",userName);//出库操作员
                                obj.put("batchCode","01");//出库批次
                                obj.put("goodsCode","");//物料编码
                                obj.put("wareCode","");//入库编码
                                obj.put("branchNum",Integer.parseInt(num));//出库支数
                                obj.put("goodesName",mName.getText().toString());//物料名称
                                obj.put("weight",wt1);//重量
                                String branchURL = "http://172.15.20.15:8083/Delivery/SaveDeliveryList";
                                String MyNews = postHttp.postdata(branchURL,obj.toString());
                                JSONObject dataObj = new JSONObject(MyNews);
                                boolean success = dataObj.getBoolean("success");
                                /*if (!success){
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/

                            JSONObject obj = new JSONObject();
                            long timeGetTime = System.currentTimeMillis();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                                    ("yyyy年MM月dd日HH时mm分ss秒", Locale.getDefault());
                            String time = simpleDateFormat.format(timeGetTime);
                            try {
                                //获取详细信息
                                String msgs = http_Get.getData(myurl+mCardId.getText().toString());
                                JSONObject obj1 = new JSONObject(msgs);
                                JSONObject result = obj1.getJSONObject("response");
                                final JSONArray mydata = result.getJSONArray("data");
                                for (int i = 0 ;i<mydata.length();i++){
                                    JSONObject message = mydata.getJSONObject(i);
                                    String car_no = message.getString("vc_no");
                                    String consigne_addr_name = message.getString("consigne_addr_name");
                                    String consigne_user_name = message.getString("consigne_user_name");
                                    String specsname = message.getString("spec_desc");
                                    String goodsname = message.getString("prod_cname");
                                    String  goodsCode = message.getString("mat_code");
                                    int branchNum = message.getInt("pick_num");
                                    Double weight = message.getDouble("wt");
                                    int entruckingCode = message.getInt("id");

                                    obj.put("car_no",car_no);//车号-装车计划
                                    obj.put("id",12);//默认主键
                                    obj.put("code","");//出库编码
                                    obj.put("methodype",1);//出库方式
                                    obj.put("consigne_addr_name",consigne_addr_name);//收货地址-装车计划
                                    obj.put("number",1);//出库件数（默认为1）
                                    obj.put("consigne_user_name",consigne_user_name);//收货客户-装车计划
                                    obj.put("specsname",specsname);//物料规格
                                    obj.put("createtime",time);//出库时间
                                    obj.put("userCode","01");//操作员编码
                                    obj.put("entruckingCode","01");//装车编码
                                    obj.put("userName","");//出库操作员
                                    obj.put("batchCode","01");//出库批次
                                    obj.put("goodsCode",goodsCode);//物料编码==商品编码
                                    obj.put("wareCode","");//入库编码
                                    obj.put("branchNum",branchNum);//出库支数
                                    obj.put("goodesName",goodsname);//物料名称
                                    obj.put("weight",weight);//重量
                                    obj.put("entruckingCode",entruckingCode);

                                    String ADD_URL = "http://172.15.20.15:8083/Entrucking/SaveEntrucking";
                                    String allMsg = postHttp.postdata(ADD_URL,obj.toString());
                                    System.out.println(allMsg);
                                    if (allMsg==null){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Main2Activity.this,"无法连接服务器",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        JSONObject object = new JSONObject(allMsg);
                                        boolean success = object.getBoolean("success");
                                        if (success){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(Main2Activity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(Main2Activity.this,"上传失败",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
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
        /*
        *listTwo = (ListView)findViewById(R.id.list_two);

        ArrayAdapter adapter1 = new ArrayAdapter(this,R.layout.list_item3);
        listTwo.setAdapter(adapter1);**/
    }
    //初始化NFC
    private void NfcBeg() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            makeText(this, "设备不支持NFC!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            makeText(this, "请在系统设置中先启用NFC功能!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //初始化PendingIntent
        // 初始化PendingIntent，当有NFC设备连接上的时候，就交给当前Activity处理
        pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    //获取数据
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 当前app正在前端界面运行，这个时候有intent发送过来，那么系统就会调用onNewIntent回调方法，将intent传送过来
        // 我们只需要在这里检验这个intent是否是NFC相关的intent，如果是，就调用处理方法
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            processIntent(intent);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pi, null, null);

    }
    //解析NFC，请求装车计划
    private void processIntent(Intent intent) {
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        assert tagFromIntent != null;
        String mCarId = ByteArrayToHexString(tagFromIntent.getId());//卡号

        //makeText(this, mCarId, Toast.LENGTH_LONG).show();
        final String myurls = "&cardCode="+mCarId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //根据卡号拉取装车计划
                    String msgs = http_Get.getData(myurl+myurls);
                    if (msgs==null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main2Activity.this,"无法连接服务器",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        JSONObject object = new JSONObject(msgs);
                        JSONObject result = object.getJSONObject("response");
                        System.out.println("一层"+result);
                        final JSONArray mydata = result.getJSONArray("data");
                        System.out.println("二层"+mydata);

                        if (mydata.isNull(0)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Main2Activity.this,"此车暂无计划",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0;i<mydata.length();i++){

                                        try {
                                            final JSONObject msg = mydata.getJSONObject(i);
                                            final String car_num = msg.getString("vc_no");//车牌号
                                            mCarNum.setText(car_num);
                                            String prod_cname = msg.getString("prod_cname");
                                            String spec_desc = msg.getString("spec_desc");//产品型号
                                            String plan_sub_no = msg.getString("plan_sub_no");//登记单号
                                            int num = msg.getInt("num");//计划数量
                                            int weight = msg.getInt("wt");//计划重量
                                            myAdapter2.add(new Data2(spec_desc, String.valueOf(num),
                                                    String.valueOf(weight),plan_sub_no));
                                            endWeight =weight + endWeight;
                                            mPlanWt.setText(String.valueOf(endWeight));//计划总重量
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        }).start();
    }
    //转为16进制字符串
    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F" };
        String out = "";


        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    private void bindViews() {
        listtwo = (ListView)findViewById(R.id.list_two);
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    private static void Analysis(String jsonStr) throws JSONException {
        JSONArray jsonArray = null;
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0;i<jsonArray.length();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("success",jsonObject.getString("success"));
            map.put("status",jsonObject.getString("status"));
            map.put("msg",jsonObject.getString("msg"));
        }
    }

}
