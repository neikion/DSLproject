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
import com.google.android.material.snackbar.Snackbar;
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
    ArrayList<BusDataSet> AlarmList=new ArrayList<>();
    int SelectedTab=0;
    public BusActivity() {
        super(new MenuCase1(), R.id.bus_root);
    }
    ViewPager2.OnPageChangeCallback pagechange =new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            tabLayout.selectTab(tabLayout.getTabAt(position));
        }
    };
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
        viewPager2.registerOnPageChangeCallback(pagechange);
        findViewById(R.id.go_bus_setting).setOnClickListener(v->{
            if(busPagerAdaptor.getItemCount()>0){
                Intent i=new Intent(BusActivity.this,BusAlarmListActivity.class);
                i.putExtra("dataSets",AlarmList);
                i.putExtra("arsId",busPagerAdaptor.getPosId(SelectedTab));
                resultLauncher.launch(i);
            }else{
                Snackbar.make(tabLayout,R.string.NeedBusStation,Snackbar.LENGTH_SHORT).show();
            }

        });
        findViewById(R.id.busmap).setOnClickListener(v -> {getBusServerData();});
        findViewById(R.id.bus_menu).setOnClickListener(v->menuLayout.openDrawer(Gravity.LEFT));
        BusConnector=new BusNotiConnector();
        bindService(new Intent(this, BusNotiService.class),BusConnector,BIND_AUTO_CREATE);
        findViewById(R.id.bus_go_set_station).setOnClickListener((v -> {
            Intent i=new Intent(BusActivity.this,SetBusStationActivity.class);
            i.putExtra("dataSets",busPagerAdaptor.getdataSets());
            stationresult.launch(i);
        }));
        getStationServerUserData();
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
    @Override
    protected void onStart() {
        super.onStart();
        if(busPagerAdaptor.getItemCount()>0){
            getBusServerData();
        }
        startService(new Intent(this,BusNotiService.class));
    }

    private void getBusServerData(){
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
    private void getStationServerUserData(){
        JSONObject json=new JSONObject();
        try{
            json.put("Id",manager.getUserCode());
            manager.sendRequest(this,json,"/getStationUserData",(result)->{
                runOnUiThread(()->{
                    if(result.length()>0){
                        for(int i=0;i<result.length();i++){
                            try {
                                busPagerAdaptor.addDataSets(result.getJSONObject(i).getString("stationid"),"content",tabLayout,result.getJSONObject(i).getString("stationname"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        ArrayList<StationDataSet> items=new ArrayList<>();
                        StationDataSet sd=new StationDataSet();
                        sd.arsID="48065";
                        sd.stationName="동서울대학교 복정 파출소 방면";
                        items.add(sd);
                        sd=new StationDataSet();
                        sd.arsID="48066";
                        sd.stationName="동서울대학교 복정 초등학교 방면";
                        items.add(sd);
                        setServerUserDataStation(items);
                        busPagerAdaptor.addDataSets("48065", "tab1", tabLayout,"동서울대학교 복정 파출소 방면");
                        busPagerAdaptor.addDataSets("48066", "tab2", tabLayout,"동서울대학교 복정 초등학교 방면");
                    }
                    getBusServerData();
                });
            });
        }catch (Exception e){

        }

    }
    private void setServerUserDataStation(ArrayList<StationDataSet> list){
        try{
            JSONObject send=new JSONObject();
            JSONObject item;
            JSONArray jsonArray=new JSONArray();
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
    private void checkAlarm(ArrayList<StationDataSet> list){
        boolean deleteNeed=false;
        for(int i=AlarmList.size()-1;i>-1;i--){
            for(int i2=0;i2<list.size();i2++){
                if(AlarmList.get(i).arsId.equals(list.get(i2).arsID)){
                    deleteNeed=false;
                    break;
                }
                deleteNeed=true;
            }
            if(deleteNeed){
                AlarmList.remove(i);
            }
        }
    }
    ActivityResultLauncher<Intent> stationresult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    SelectedTab=-1;
                    viewPager2.setCurrentItem(SelectedTab);
                    ArrayList<StationDataSet> dataSets= (ArrayList<StationDataSet>) result.getData().getSerializableExtra("dataSets");
                    checkAlarm(dataSets);
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
            getBusServerData();
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
        viewPager2.unregisterOnPageChangeCallback(pagechange);
        BusConnector.getService().closeService();
        unbindService(BusConnector);
        super.onDestroy();
    }
}