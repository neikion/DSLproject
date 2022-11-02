package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dsl.R;
import com.example.dsl.bus.BusActivity;
import com.example.dsl.calender.CalenderActivity;
import com.example.dsl.schedule.Schedule;
import com.example.dsl.weather.WeatherActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViewById(R.id.txtMenuGroup1).setOnClickListener(this);
        findViewById(R.id.btnSchedule).setOnClickListener(this);
        findViewById(R.id.btnPrevious).setOnClickListener(this);
        findViewById(R.id.btnConfig).setOnClickListener(this);
        findViewById(R.id.btnbus).setOnClickListener(this);
        findViewById(R.id.txtMenuGroup2).setOnClickListener(this);
        findViewById(R.id.btnCalendar).setOnClickListener(this);
        findViewById(R.id.btnWeather).setOnClickListener(this);
        findViewById(R.id.notice).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        Intent i;
        if(id==R.id.notice){
            i=new Intent(getApplicationContext(), NoticeActivity.class);
        }else if(id==R.id.btnSchedule){
            i=new Intent(getApplicationContext(), Schedule.class);
        }else if(id==R.id.btnCalendar){
            i=new Intent(getApplicationContext(), CalenderActivity.class);
        }else if(id==R.id.btnWeather){
            i=new Intent(getApplicationContext(), WeatherActivity.class);
        }else if(id==R.id.btnConfig){
            i = new Intent(getApplicationContext(), ConfMenuActivity.class);
        }else if(id==R.id.btnbus){
            i = new Intent(getApplicationContext(), BusActivity.class);
        }
        else{
            finish();
            return;
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}