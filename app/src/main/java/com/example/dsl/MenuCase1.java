package com.example.dsl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dsl.bus.BusActivity;
import com.example.dsl.calender.CalenderActivity;
import com.example.dsl.notice.ConfMenuActivity;
import com.example.dsl.notice.LoginActivity;
import com.example.dsl.notice.NoticeActivity;
import com.example.dsl.roompos.RoomPosition;
import com.example.dsl.schedule.Schedule;
import com.example.dsl.weather.WeatherActivity;

public class MenuCase1 implements MenuFrame {

    @Override
    public void onStart(View v) {
        v.findViewById(R.id.txtMenuGroup1).setOnClickListener(this);
        v.findViewById(R.id.btnSchedule).setOnClickListener(this);
        v.findViewById(R.id.btnlogout).setOnClickListener(this);
        v.findViewById(R.id.btnConfig).setOnClickListener(this);
        v.findViewById(R.id.btnbus).setOnClickListener(this);
        v.findViewById(R.id.txtMenuGroup2).setOnClickListener(this);
        v.findViewById(R.id.btnCalendar).setOnClickListener(this);
        v.findViewById(R.id.btnWeather).setOnClickListener(this);
        v.findViewById(R.id.notice).setOnClickListener(this);
        v.findViewById(R.id.btnRoomPos).setOnClickListener(this);
        v.findViewById(R.id.menu_root).setOnTouchListener((v1, event) -> true);
    }

    @Override
    @CallSuper
    public void onClick(View v) {
        int id=v.getId();
        Intent i=null;
        if(id==R.id.notice){
            i=new Intent(v.getContext(), NoticeActivity.class);
        }else if(id==R.id.btnSchedule){
            i=new Intent(v.getContext(), Schedule.class);
        }else if(id==R.id.btnCalendar){
            i=new Intent(v.getContext(), CalenderActivity.class);
        }else if(id==R.id.btnWeather){
            i=new Intent(v.getContext(), WeatherActivity.class);
        }else if(id==R.id.btnConfig){
            i = new Intent(v.getContext(), ConfMenuActivity.class);
        }else if(id==R.id.btnbus){
            i = new Intent(v.getContext(), BusActivity.class);
        }else if(id==R.id.btnlogout){
            i = new Intent(v.getContext(), LoginActivity.class);
        }else if(id==R.id.btnRoomPos){
            i = new Intent(v.getContext(), RoomPosition.class);
        }
        if(i!=null){
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            v.getContext().startActivity(i);
        }
    }
}
