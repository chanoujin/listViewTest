package com.example.listviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main5Activity extends AppCompatActivity {
    private Button bt1,bt2,bt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        bt1 = findViewById(R.id.button5);
        bt2 = findViewById(R.id.button8);
        bt3 = findViewById(R.id.button9);
        //装车
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main5Activity.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        //拆分
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main5Activity.this,Main4Activity.class);
                startActivity(intent);
            }
        });
        //入库
        //bt3.setOnClickListener((View.OnClickListener) this);
    }
}
