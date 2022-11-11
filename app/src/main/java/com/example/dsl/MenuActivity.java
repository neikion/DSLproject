package com.example.dsl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dsl.bus.BusActivity;
import com.example.dsl.calender.CalenderActivity;
import com.example.dsl.notice.ConfMenuActivity;
import com.example.dsl.notice.NoticeActivity;
import com.example.dsl.schedule.Schedule;
import com.example.dsl.weather.WeatherActivity;

public class MenuActivity extends MenuBaseActivity implements View.OnClickListener{

    public MenuActivity(){
        super(new MenuCase1(),R.id.menu_root);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}