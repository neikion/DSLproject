package com.example.dsl.bus;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.dsl.DSLManager;
import com.example.dsl.MenuBaseActivity;
import com.example.dsl.MenuCase1;
import com.example.dsl.R;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BusActivity extends MenuBaseActivity implements TabLayout.OnTabSelectedListener {

    DSLManager manager;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    BusPagerAdaptor busPagerAdaptor;
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
            Intent i=new Intent(getApplicationContext(),BusAlarmListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        busPagerAdaptor.addDataSets("tab1");
        busPagerAdaptor.addDataSets("tab2");
        findViewById(R.id.busmap).setOnClickListener(v -> getServerData());
        findViewById(R.id.bus_menu).setOnClickListener(v->menuLayout.openDrawer(Gravity.LEFT));
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
}