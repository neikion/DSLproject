package com.example.dsl.bus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ChooseBusStationActivity extends AppCompatActivity {
    RecyclerView rv;
    BusStationSettingAdaptor adaptor;
    EditText searchEt;

    BusStationSettingAdaptor.action listner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_station_add);
        listner=new BusStationSettingAdaptor.action() {
            @Override
            public void action(int position, ArrayList<StationDataSet> list) {
                Intent i=new Intent();
                i.putExtra("dataSet",list.get(position));
                setResult(RESULT_OK,i);
                finish();
            }
        };
        rv=findViewById(R.id.bus_set_station_list);
        LinearLayoutManager lm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rv.setLayoutManager(lm);
        adaptor=new BusStationSettingAdaptor(null, null);
        rv.setAdapter(adaptor);
        searchEt=findViewById(R.id.bus_station_search_et);
        findViewById(R.id.bus_station_search_btn).setOnClickListener(v->{
            JSONObject json=new JSONObject();
            try {
                json.put("Id",searchEt.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DSLManager.getInstance().sendRequest(ChooseBusStationActivity.this, json, "/getBusStation", new DSLManager.NetListener() {
                @Override
                public void Result(JSONArray Result) {
                    JSONObject json;
                    for(int i=0;i<Result.length();i++){
                        try {
                            json=Result.getJSONObject(i);
                            StationDataSet dataSet=new StationDataSet(json.getString("arsId"), URLDecoder.decode(json.getString("stNm"),"utf-8"));
                            runOnUiThread(()->{
                                adaptor.add(dataSet, listner);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });

        findViewById(R.id.bus_go_main).setOnClickListener(v->{
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}