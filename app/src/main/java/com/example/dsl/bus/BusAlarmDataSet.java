package com.example.dsl.bus;

import com.google.gson.JsonObject;

public class BusAlarmDataSet {
    String AlarmTitle="";
    String AlarmBus="";
    int time=0;

    public String getStringTime(){
        return String.valueOf(time);
    }
    public void setAlarmDataSet(JsonObject jsonObject){

    }
}
