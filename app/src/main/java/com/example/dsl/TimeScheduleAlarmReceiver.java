package com.example.dsl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class TimeScheduleAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DSL","GetReceive : "+new Date(System.currentTimeMillis()));
        if(intent.getBooleanExtra("AP",false)){
            Intent starts=new Intent(context, TimeScheduleAlarmService.class);
            starts.putExtra("AP",true);
            context.startForegroundService(starts);
        }else{
            Intent starts=new Intent(context, TimeScheduleAlarmService.class);
            starts.putExtra("AP",false);
            context.startService(starts);
        }


    }
}
