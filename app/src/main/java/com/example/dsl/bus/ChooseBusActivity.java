package com.example.dsl.bus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChooseBusActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BusListAdaptor busListAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bus);
        findViewById(R.id.bus_choose_bus_cancle).setOnClickListener(v->{
            setResult(RESULT_CANCELED);
            finish();
        });
        recyclerView=findViewById(R.id.bus_choose_list_view);
        LinearLayoutManager lm=new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(lm);
        busListAdaptor=new BusListAdaptor();
        JSONObject getStationRoute=new JSONObject();
        try {
            getStationRoute.put("ID",getIntent().getStringExtra("arsId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        DSLManager.getInstance().sendRequest(getApplicationContext(), getStationRoute, "/getStationRoute", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    for(int i=0;i<Result.length();i++){
                        try {
                            busListAdaptor.addDataset(Result.getJSONObject(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        recyclerView.setAdapter(busListAdaptor);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}