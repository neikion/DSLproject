package com.example.dsl.schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.dsl.R;

import java.util.Locale;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    TextView endtv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        init();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("DSL","main onDestroy");

    }
    public void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        endtv =findViewById(R.id.end);
        endtv.setOnClickListener(this);
        Intent intent=getIntent();
        String ClockTime=String.format(Locale.getDefault(),"%02d : %02d",intent.getIntExtra("HOUR_OF_DAY",0),intent.getIntExtra("MINUTE",0));
        endtv.setText(ClockTime);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.end){
            startService(new Intent(this,TimeScheduleAlarmService.class).putExtra("AP",false));
            finish();
            /*android.os.Process.killProcess(android.os.Process.myPid());*/
        }
    }
}