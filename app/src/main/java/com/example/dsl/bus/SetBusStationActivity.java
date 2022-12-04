package com.example.dsl.bus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.xml.transform.Result;

public class SetBusStationActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==RESULT_OK){
                adaptor.add((StationDataSet) result.getData().getSerializableExtra("dataSet"),listner);
                setServerUserDataStation();
            }
        }
    });

    private RecyclerView rv;
    private BusStationSettingAdaptor adaptor;
    BusStationSettingAdaptor.action listner;
    BusNotiConnector connector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_station);
        connector=new BusNotiConnector();
        bindService(new Intent(this, BusNotiService.class),connector,BIND_AUTO_CREATE);
        listner=new BusStationSettingAdaptor.action() {
            @Override
            public void action(int position, ArrayList<StationDataSet> list) {
                Snackbar.make(rv,R.string.DeleteMassage,Snackbar.LENGTH_SHORT);
                connector.getService().removeConstraintStation(list.get(position).arsID);
                deleteAlarmAndBusServerUserData(list.get(position).arsID);
                list.remove(position);
                adaptor.notifyItemRemoved(position);
            }
        };
        findViewById(R.id.bus_go_main).setOnClickListener(v -> {

            Intent i=new Intent();
            i.putExtra("dataSets",adaptor.getDataSets());
            setResult(RESULT_OK,i);
            finish();
        });
        findViewById(R.id.bus_add_station).setOnClickListener(v->{
            Intent i=new Intent(this,ChooseBusStationActivity.class);
            resultLauncher.launch(i);
        });
        rv=findViewById(R.id.bus_station_list);
        LinearLayoutManager lm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rv.setLayoutManager(lm);
        ArrayList<StationDataSet> dataSets=(ArrayList<StationDataSet>) getIntent().getSerializableExtra("dataSets");
        if(dataSets!=null&&dataSets.size()>0) {
            adaptor = new BusStationSettingAdaptor(dataSets, listner);
        }else{
            adaptor=new BusStationSettingAdaptor();
        }
        rv.setAdapter(adaptor);

    }

    @Override
    protected void onDestroy() {
        unbindService(connector);
        super.onDestroy();
    }
    private void deleteAlarmAndBusServerUserData(String stationid){
        try{
            JSONObject json=new JSONObject();
            json.put("usercode",DSLManager.getInstance().getUserCode());
            json.put("stationid",stationid);
            JSONObject send=new JSONObject();
            send.put("id",json);
            DSLManager.getInstance().sendRequest(this, send, "/deleteStationUserData",null);
        }catch (Exception e){

        }
    }
    private void setServerUserDataStation(){
        try{
            JSONObject send=new JSONObject();
            JSONObject item;
            JSONArray jsonArray=new JSONArray();
            ArrayList<StationDataSet> list= adaptor.getDataSets();
            int usercode=DSLManager.getInstance().getUserCode();
            for(int i=0;i<list.size();i++){
                item=new JSONObject();
                item.put("usercode",usercode);
                item.put("stationid",list.get(i).arsID);
                item.put("stationname",list.get(i).stationName);
                jsonArray.put(item);
            }
            send.put("id",jsonArray);
            DSLManager.getInstance().sendRequest(this,send,"/setStationUserData",null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}