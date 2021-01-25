package com.example.listviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity {
    private List<String> listdata = null;
    private ListView listView;
    private EditText inputT;
    private TextView mName,mModel,mNumber;
    private Button bt,bt2;
    private int nowNum = 0;
    private int planNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        listdata = new ArrayList<String>();
        ListAdapter adapter = new ArrayAdapter<String>(Main4Activity.this,
                android.R.layout.simple_list_item_1,listdata);
        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        inputT = findViewById(R.id.editText);
        mName = findViewById(R.id.name);
        mModel = findViewById(R.id.model);
        mNumber = findViewById(R.id.number);
        bt = findViewById(R.id.button6);
        bt2 = findViewById(R.id.button7);
        planNum = Integer.parseInt(inputT.getText().toString());
        nowNum = Integer.parseInt(mNumber.getText().toString());
        //拆分入库
        if (mName!=null){
            if ( nowNum>planNum){
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listdata.add(inputT.getText().toString()+"件");
                        int x = nowNum-planNum;
                        mNumber.setText(String.valueOf(x));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("number",planNum);
                                    String news = newHttp.LoginPost(object,"daksjd");//提交打印要求,并入库*************
                                    JSONObject object1 = new JSONObject(news);//确认是否上传成功
                                    boolean endsmg = object1.getBoolean("result");
                                    if (endsmg){
                                        Toast.makeText(Main4Activity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                        //拆分打包好入库


                                    }
                                    else {
                                        Toast.makeText(Main4Activity.this,"打印失败",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();
                    }
                });
            }
        }
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
