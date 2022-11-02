package com.example.dsl.bus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusActivity extends AppCompatActivity {

    DSLManager manager;
    TextView schooldir,campusdir;
    JSONArray test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager=DSLManager.getInstance();
        setContentView(R.layout.activity_bus);
        schooldir=findViewById(R.id.schoolDir);
        campusdir=findViewById(R.id.campusDir);
        findViewById(R.id.bus_menu).setOnClickListener(v->DSLManager.gomenu(getApplicationContext()));
        findViewById(R.id.busmap).setOnClickListener(v -> manager.sendRequest(getApplicationContext(), null, "/BusState", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    try {
                        campusdir.setText(getData(Result.getJSONArray(0)));
                        schooldir.setText(getData(Result.getJSONArray(1)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }));

    }

    public String getData(JSONArray json) throws JSONException {
        JSONObject data;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<json.length();i++){
            data=json.getJSONObject(i);
            sb.append(data.get("routeName")).append(" 버스 ").append(data.get("predictTime1")).append("분 후 도착\n");
        }
        return sb.toString();
    }
    @Override
    protected void onStart() {
        super.onStart();
        manager.sendRequest(getApplicationContext(), null, "/BusState", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    try {
                        campusdir.setText(getData(Result.getJSONArray(0)));
                        schooldir.setText(getData(Result.getJSONArray(1)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}