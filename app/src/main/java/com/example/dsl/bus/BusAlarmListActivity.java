package com.example.dsl.bus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BusAlarmListActivity extends AppCompatActivity {

    RecyclerView rv;
    BusAlarmListAdaptor busAlarmAdaptor;
    BusNotiConnector BusConnector;
    boolean resultOK=false;
    ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                Intent ReIntent=result.getData();
                BusDataSet dataSet=(BusDataSet) ReIntent.getSerializableExtra("BusAlarmData");
                busAlarmAdaptor.addBusDataSet(dataSet,(position,list)->{
                    BusDataSet target=list.get(position);
                    BusConnector.getService().removeConstraintBusData(target.arsId,target.BusName);
                    list.remove(position);
                    DSLUtil.print(list.size());
                    setServerUserDataAlarm(list);
                });
                BusConnector.getService().addConstraintBusData(dataSet);
                Intent i=new Intent();
                i.putExtra("dataSets",busAlarmAdaptor.getDataSets());
                resultOK=true;
                setResult(Activity.RESULT_OK,i);
                setServerUserDataAlarm(busAlarmAdaptor.getDataSets());
            }else{
                resultOK=false;
            }

        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_alarm_list);
        resultOK=false;
        findViewById(R.id.bus_go_main).setOnClickListener(v->{
            Intent i=new Intent();
            i.putExtra("dataSets",busAlarmAdaptor.getDataSets());
            if(!resultOK){
                setResult(Activity.RESULT_CANCELED,i);
            }
            finish();
        });
        findViewById(R.id.bus_addalarm).setOnClickListener(v->{
            Intent i=new Intent(BusAlarmListActivity.this,BusSettingActivity.class);
            i.putExtra("arsId",getIntent().getStringExtra("arsId"));
            resultLauncher.launch(i);
        });
        rv=findViewById(R.id.bus_alarm_list);
        LinearLayoutManager lm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rv.setLayoutManager(lm);
        busAlarmAdaptor=new BusAlarmListAdaptor();
        ArrayList<BusDataSet> list=(ArrayList<BusDataSet>) getIntent().getSerializableExtra("dataSets");
        for(int i=0;i<list.size();i++){
            busAlarmAdaptor.addBusDataSet(list.get(i),(position,datalist)->{
                BusDataSet target=datalist.get(position);
                BusConnector.getService().removeConstraintBusData(target.arsId,target.BusName);
                list.remove(position);
                setServerUserDataAlarm(list);
            });
        }
        rv.setAdapter(busAlarmAdaptor);
        rv.setHasFixedSize(true);
        BusConnector=new BusNotiConnector();
        bindService(new Intent(this, BusNotiService.class),BusConnector,BIND_AUTO_CREATE);
    }
    private void setServerUserDataAlarm(ArrayList<BusDataSet> list){
        try{
            JSONArray items=new JSONArray();
            JSONObject item;
            JSONObject send=new JSONObject();
            int usercode= DSLManager.getInstance().getUserCode();
            if(list.size()>0){
                for(int i=0;i<list.size();i++){
                    item=new JSONObject();
                    item.put("usercode",usercode);
                    item.put("busname",list.get(i).BusName);
                    item.put("stationid",list.get(i).arsId);
                    item.put("alarm",0);
                    item.put("alarmname",list.get(i).AlarmName);
                    items.put(item);
                }
            }else{
                item=new JSONObject();
                item.put("usercode",usercode);
                items.put(item);
            }
            send.put("dataSets",items);
            DSLManager.getInstance().sendRequest(this,send,"/setBusUserData",null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        unbindService(BusConnector);
        super.onDestroy();
    }
}