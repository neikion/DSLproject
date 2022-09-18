package com.example.dsl;

import static com.example.dsl.DSLUtil.print;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

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
        table=new TimeTable(this,BaseTablePosition,UITablePosition,4);
        Recyclerinit(timestart);
        TempReciveIntent();
    }
    public void TempReciveIntent(){
        Intent receive=getIntent();
        ArrayList<TSListAdaptor.AdaptorDataSet> list= (ArrayList<TSListAdaptor.AdaptorDataSet>) receive.getSerializableExtra("LegacySticker");
        int modifydatasetindex=receive.getIntExtra("addLegacySticker",-1);
        TSListAdaptor.AdaptorDataSet modifydataset;

        if(list!=null){
            if(modifydatasetindex!=-1){
                modifydataset=list.get(modifydatasetindex+1);
                list.remove(modifydatasetindex+1);
                table.setLegacyStickers(list);
                ta.clearArrayData();
                ta.setArrayData(0,list.get(0));
                ta.setArrayData(1,modifydataset);
                ta.setArrayData(2,list.get(list.size()-1));
                table.setOnReadyStickers(ta.getArrayData());
            }else{
                table.setLegacyStickers(list);
                table.setOnReadyStickers(new ArrayList<>());
            }
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
    private boolean check(){
        ArrayList<TSListAdaptor.AdaptorDataSet> legacy=table.getLefacyStickers();
        ArrayList<TSListAdaptor.AdaptorDataSet> modify=ta.getArrayData();
        int mday=0,lday=0;
        Calendar lsc=Calendar.getInstance();
        Calendar lec=Calendar.getInstance();
        Calendar msc=Calendar.getInstance();
        Calendar mec=Calendar.getInstance();

        for(int i=1;i<modify.size()-1;i++){
            mday=Integer.parseInt(modify.get(i).datas[0]);
            msc.set(Calendar.HOUR_OF_DAY,Integer.parseInt(modify.get(i).datas[1])/100);
            msc.set(Calendar.MINUTE,Integer.parseInt(modify.get(i).datas[1])%100);
            mec.set(Calendar.HOUR_OF_DAY,Integer.parseInt(modify.get(i).datas[2])/100);
            mec.set(Calendar.MINUTE,Integer.parseInt(modify.get(i).datas[2])%100);
            if(legacy!=null&&legacy.size()>2){
                for(int i2=1;i2<legacy.size()-1;i2++){
                    lday=Integer.parseInt(legacy.get(i2).datas[0]);
                    lsc.set(Calendar.HOUR_OF_DAY,Integer.parseInt(legacy.get(i2).datas[1])/100);
                    lsc.set(Calendar.MINUTE,Integer.parseInt(legacy.get(i2).datas[1])%100);
                    lec.set(Calendar.HOUR_OF_DAY,Integer.parseInt(legacy.get(i2).datas[2])/100);
                    lec.set(Calendar.MINUTE,Integer.parseInt(legacy.get(i2).datas[2])%100);
                    if(mday!=lday){
                        continue;
                    }
                    if(calendercheck(msc,mec,lsc,lec)){
                        return true;
                    }
                }
            }
            if(i==modify.size()-2){
                continue;
            }
            for(int i4=i+1;i4<modify.size()-1;i4++){
                lday=Integer.parseInt(modify.get(i4).datas[0]);
                lsc.set(Calendar.HOUR_OF_DAY,Integer.parseInt(modify.get(i4).datas[1])/100);
                lsc.set(Calendar.MINUTE,Integer.parseInt(modify.get(i4).datas[1])%100);
                lec.set(Calendar.HOUR_OF_DAY,Integer.parseInt(modify.get(i4).datas[2])/100);
                lec.set(Calendar.MINUTE,Integer.parseInt(modify.get(i4).datas[2])%100);
                if(mday!=lday){
                    continue;
                }
                if(calendercheck(msc,mec,lsc,lec)){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean calendercheck(Calendar msc,Calendar mec,Calendar lsc,Calendar lec){
        //msc가 더 작은 경우
        if(msc.compareTo(lsc)<=0){
            if(lsc.compareTo(mec)<0){
                return true;
            }
        }
        if(lsc.compareTo(msc)<=0){
            if(msc.compareTo(lec)<0){
                return true;
            }
        }
        return false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_cancel:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            case R.id.add_accept:
                if(!check()){
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
                        if(table.getLefacyStickers()!=null){
                            Result.putExtra("Result_Value",table.getLefacyStickers());
                            setResult(Activity.RESULT_OK,Result);
                        }else{
                            setResult(Activity.RESULT_CANCELED);
                        }
                    }
                    finish();
                }else{
                    Snackbar s= Snackbar.make(BaseTablePosition,R.string.TimeSchdule_Sticker_Overlap_Toast,Snackbar.LENGTH_SHORT);
                    s.getView().setTranslationY(-DSLUtil.DPtoPX(10,this));
                    s.show();
                }
                break;
        }
    }
}