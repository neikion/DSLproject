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

import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import javax.xml.transform.Result;

public class SetBusStationActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==RESULT_OK){
                adaptor.add((StationDataSet) result.getData().getSerializableExtra("dataSet"),listner);

            }
        }
    });

    RecyclerView rv;
    BusStationSettingAdaptor adaptor;
    BusStationSettingAdaptor.action listner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_station);
        listner=new BusStationSettingAdaptor.action() {
            @Override
            public void action(int position, ArrayList<StationDataSet> list) {
                Snackbar.make(rv,R.string.DeleteMassage,Snackbar.LENGTH_SHORT);
                list.remove(position);
                adaptor.notifyItemRemoved(position);
            }
        };
        findViewById(R.id.bus_go_main).setOnClickListener(v -> {
            Intent i=new Intent();
            i.putExtra("dataSets",adaptor.dataSets);
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
        }
        rv.setAdapter(adaptor);

    }
}