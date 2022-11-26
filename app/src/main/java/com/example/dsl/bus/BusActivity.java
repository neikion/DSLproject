package com.example.dsl.bus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
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
    int SelectedTab=0;
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
            i.putExtra("arsId",busPagerAdaptor.getPosId(SelectedTab));
            resultLauncher.launch(i);
        });
        findViewById(R.id.busmap).setOnClickListener(v -> getServerData());
        findViewById(R.id.bus_menu).setOnClickListener(v->menuLayout.openDrawer(Gravity.LEFT));
        BusConnector=new BusNotiConnector();
        bindService(new Intent(this, BusNotiService.class),BusConnector,BIND_AUTO_CREATE);
        findViewById(R.id.bus_go_set_station).setOnClickListener((v -> {
            Intent i=new Intent(BusActivity.this,SetBusStationActivity.class);
            i.putExtra("dataSets",busPagerAdaptor.dataSets);
            stationresult.launch(i);
        }));
        busPagerAdaptor.addDataSets("48065", "tab1", tabLayout,"동서울대학교 복정 파출소 방면");
        busPagerAdaptor.addDataSets("48066", "tab2", tabLayout,"동서울대학교 복정 초등학교 방면");
        manager.CreatenotiChannel(this);
    }

    public String refineData(JSONArray json) throws JSONException {
        JSONObject data;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<json.length();i++){
            data=json.getJSONObject(i);
            try {
                sb.append(data.get("busRouteAbrv")).append(" 버스 ").append(URLDecoder.decode(data.get("arrmsg1").toString(),"utf-8")).append("\n");
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
                    datastr=datastr.replaceAll("[^0-9]*","");
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
        String Id=busPagerAdaptor.getPosId(SelectedTab);
        JSONObject json=new JSONObject();
        try {
            json.put("Id",Id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        manager.sendRequest(getApplicationContext(), json, "/getBusPosition", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    try {
                        busPagerAdaptor.setDataSets(Id,refineData(Result));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
    ActivityResultLauncher<Intent> stationresult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    SelectedTab=0;
                    viewPager2.setCurrentItem(SelectedTab);
                    ArrayList<StationDataSet> dataSets= (ArrayList<StationDataSet>) result.getData().getSerializableExtra("dataSets");
                    busPagerAdaptor.setDataSets(dataSets,tabLayout);
                }
            }
        }
    );
    ActivityResultLauncher < Intent > resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                String Id=busPagerAdaptor.getPosId(SelectedTab);
                JSONObject json=new JSONObject();
                try {
                    json.put("Id",Id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                manager.sendRequest(BusActivity.this, json, "/getBusPosition", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        runOnUiThread(() -> {
                            try {
                                busPagerAdaptor.setDataSets(Id,refineData(Result));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        try {
                            BusConnector.getService().startAlarm();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (result.getData() != null) {
                if (result.getData().hasExtra("dataSets")) {
                    AlarmList = (ArrayList<BusDataSet>) result.getData().getSerializableExtra("dataSets");
                }
            }
        }
    });

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        SelectedTab=tab.getPosition();
        viewPager2.setCurrentItem(tab.getPosition());
        if(busPagerAdaptor.getIdCount()>0){
            getServerData();
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        BusConnector.getService().startAlarm();
    }


    @Override
    protected void onDestroy() {
        BusConnector.getService().closeService();
        unbindService(BusConnector);
        super.onDestroy();
    }
}