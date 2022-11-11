package com.example.dsl.bus;

import static com.example.dsl.DSLUtil.print;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dsl.R;
import com.google.android.material.snackbar.Snackbar;

public class BusSettingActivity extends AppCompatActivity {

    boolean vibe=false;
    String choosedBus="";
    TextView settingName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_setting);
        settingName=findViewById(R.id.bus_setting_name);
        findViewById(R.id.bus_alarm_cancle).setOnClickListener(v->{
            setResult(RESULT_CANCELED);
            finish();
        });
        findViewById(R.id.bus_alarm_ok).setOnClickListener(v->{
            if(settingName.getText().toString().isEmpty()){
                Snackbar.make(settingName,R.string.Bus_Alarm_Setting_Name_Empty,Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(choosedBus.isEmpty()){
                Snackbar.make(settingName,R.string.Bus_Alarm_Setting_Bus_Name_Empty,Snackbar.LENGTH_SHORT).show();
                return;
            }
            Intent i=new Intent();
            i.putExtra("AlarmName",settingName.getText().toString());
            i.putExtra("vibe",vibe);
            i.putExtra("choosedBus",choosedBus);
            setResult(RESULT_OK,i);
            finish();
        });
        ((Switch)findViewById(R.id.bus_alarm_vibe)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    vibe=!vibe;
                }
            }
        });
        findViewById(R.id.bus_setting_bus).setOnClickListener(v->{
            activityResultLauncher.launch(new Intent(getApplicationContext(),ChooseBusActivity.class));
        });
    }
    ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                choosedBus=result.getData().getStringExtra("BusName");
                ((TextView)findViewById(R.id.bus_setting_choose_bus)).setText(choosedBus);
            }
        }
    });
}