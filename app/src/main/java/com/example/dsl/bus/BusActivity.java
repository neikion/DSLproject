package com.example.dsl.bus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.MenuBaseActivity;
import com.example.dsl.MenuCase1;
import com.example.dsl.R;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class BusActivity extends MenuBaseActivity implements TabLayout.OnTabSelectedListener {

    DSLManager manager;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    BusPagerAdaptor busPagerAdaptor;
    BusNotiConnector BusConnector;
    ArrayList<BusDataSet> AlarmList;
    public BusActivity() {
        super(new MenuCase1(), R.id.bus_root);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        manager=DSLManager.getInstance();
        tabLayout=findViewById(R.id.bus_tab_root);
        tabLayout.addOnTabSelectedListener(this);
        viewPager2=findViewById(R.id.bus_pager_root);
        busPagerAdaptor=new BusPagerAdaptor();
        viewPager2.setAdapter(busPagerAdaptor);
        findViewById(R.id.go_bus_setting).setOnClickListener(v->{
            Intent i=new Intent(BusActivity.this,BusAlarmListActivity.class);
            if(AlarmList!=null){
                i.putExtra("dataSets",AlarmList);
            }
            resultLauncher.launch(i);
        });
        busPagerAdaptor.addDataSets("tab1");
        busPagerAdaptor.addDataSets("tab2");
        findViewById(R.id.busmap).setOnClickListener(v -> getServerData());
        findViewById(R.id.bus_menu).setOnClickListener(v->menuLayout.openDrawer(Gravity.LEFT));
        BusConnector=new BusNotiConnector();
        bindService(new Intent(this, BusNotiService.class),BusConnector,BIND_AUTO_CREATE);

    }

    public String refineData(JSONArray json) throws JSONException {
        JSONObject data;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<json.length();i++){
            data=json.getJSONObject(i);
            try {
                sb.append(data.get("busRouteAbrv")).append(" 버스 ").append(URLDecoder.decode(data.get("arrmsg1").toString(),"utf-8").toString()).append("\n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public ArrayList<BusDataSet> refineDatatoBusDataSet(JSONArray json) throws Exception{
        JSONObject data;
        ArrayList<BusDataSet> result=new ArrayList<>();
        BusDataSet Busdata;
        String datastr;
        int timeindex=-1;
        for(int i=0;i<json.length();i++){
            data=json.getJSONObject(i);
            Busdata=new BusDataSet();
            try {
                Busdata.BusName=data.getString("busRouteAbrv");
                datastr=data.getString("arrmsg1");
                timeindex=datastr.indexOf("분");
                if(timeindex!=-1){
                    datastr=datastr.substring(0,timeindex);
                    datastr=datastr.replaceAll("[^1-9]*","");
                    Busdata.time=Integer.parseInt(datastr);
                    result.add(Busdata);
                }else{
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    @Override
    protected void onStart() {
        super.onStart();
        getServerData();

    }

    private void getServerData(){
        manager.sendRequest(getApplicationContext(), null, "/getBusPosition", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    try {
                        busPagerAdaptor.setDataSets(0, refineData(Result.getJSONArray(0)));
                        busPagerAdaptor.setDataSets(1, refineData(Result.getJSONArray(1)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
    ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                manager.sendRequest(BusActivity.this, null, "/getBusPosition", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        runOnUiThread(()->{
                            try {
                                busPagerAdaptor.setDataSets(0, refineData(Result.getJSONArray(0)));
                                busPagerAdaptor.setDataSets(1, refineData(Result.getJSONArray(1)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        try {
                            BusConnector.getService().setBusState(refineDatatoBusDataSet(Result.getJSONArray(0)));
                            BusConnector.getService().runable();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if(result.getData()!=null){
                if(result.getData().hasExtra("dataSets")){
                    AlarmList=(ArrayList<BusDataSet>) result.getData().getSerializableExtra("dataSets");
                }
            }
        }
    });

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition()==0){
            viewPager2.setCurrentItem(0);
        }else{
            viewPager2.setCurrentItem(1);
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onDestroy() {
        unbindService(BusConnector);
        super.onDestroy();
    }
}