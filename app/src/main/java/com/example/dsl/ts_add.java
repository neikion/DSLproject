package com.example.dsl;

import static com.example.dsl.DSLUtil.print;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ts_add extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rv;
    TSListAdaptor ta;
    LinearLayout BaseTablePosition;
    LinearLayout UITablePosition;
    TimeTable table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ts_add);
        init(9);
    }
    public void init(int timestart){
        findViewById(R.id.add_cancel).setOnClickListener(this);
        findViewById(R.id.add_accept).setOnClickListener(this);
        UITablePosition=findViewById(R.id.ts_add_ui_table);
        BaseTablePosition=findViewById(R.id.ts_add_base_table);
        table=new TimeTable(this,BaseTablePosition,UITablePosition);
        Recyclerinit(timestart);
        TempReciveIntent();
    }
    public void TempReciveIntent(){
        Intent receive=getIntent();
        ArrayList<TSListAdaptor.AdaptorDataSet> list= (ArrayList<TSListAdaptor.AdaptorDataSet>) receive.getSerializableExtra("LegacySticker");
        if(list!=null){
            table.setLegacyStickers(list);
        }
    }
    public void Recyclerinit(int timestart){
        rv=findViewById(R.id.time_setting);
        LinearLayoutManager lm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rv.setLayoutManager(lm);
        ta=new TSListAdaptor(this,0,timestart,timestart+1,table);
        rv.setAdapter(ta);
        rv.setHasFixedSize(true);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_cancel:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            case R.id.add_accept:
                Intent Result=new Intent();
                if(ta.getArrayData().size()>2){
                    if(table.getLefacyStickers()!=null){
                        ArrayList<TSListAdaptor.AdaptorDataSet> all=ta.getArrayData();
                        for(int i=1;i<table.getLefacyStickers().size()-1;i++){
                            all.add(all.size()-2,table.getLefacyStickers().get(i));
                        }
                        Result.putExtra("Result_Value",all);
                    }else{
                        Result.putExtra("Result_Value",ta.getArrayData());
                    }
                    setResult(Activity.RESULT_OK,Result);
                }else{
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();
                break;
        }
    }
}