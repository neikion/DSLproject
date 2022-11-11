package com.example.dsl.bus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dsl.R;
import com.example.dsl.schedule.TSListAdaptor;

public class BusAlarmListActivity extends AppCompatActivity {

    RecyclerView rv;
    BusAlarmAdaptor busAlarmAdaptor;
    ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                Intent ReIntent=result.getData();

            }else if(result.getResultCode()==Activity.RESULT_CANCELED){
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_alarm_list);
        findViewById(R.id.bus_go_main).setOnClickListener(v->finish());
        findViewById(R.id.bus_addalarm).setOnClickListener(v->{
            Intent i=new Intent(getApplicationContext(),BusSettingActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultLauncher.launch(i);
        });
        rv=findViewById(R.id.bus_alarm_list);
        LinearLayoutManager lm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rv.setLayoutManager(lm);
        busAlarmAdaptor=new BusAlarmAdaptor();
        rv.setAdapter(busAlarmAdaptor);
        rv.setHasFixedSize(true);
    }
}