package com.example.dsl.schedule;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

public class TimeScheduleAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DSL","GetReceive : "+new Date(System.currentTimeMillis()));
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //재부팅시 들어옴
        }else{
            if(intent.getBooleanExtra("AP",false)){
                Intent starts=new Intent(context, TimeScheduleAlarmService.class);
                starts.putExtra("AlarmGroup",intent.getIntExtra("AlarmGroup",0));
                starts.putExtra("AP",true);
                starts.putExtra("Request_Code",intent.getIntExtra("Request_Code",0));
                starts.putExtra("DAY_OF_WEEK",intent.getIntExtra("DAY_OF_WEEK",0));
                starts.putExtra("HOUR_OF_DAY",intent.getIntExtra("HOUR_OF_DAY",0));
                starts.putExtra("MINUTE",intent.getIntExtra("MINUTE",0));
                context.startService(starts);
            }else{
                Intent starts=new Intent(context, TimeScheduleAlarmService.class);
                starts.putExtra("AP",false);
                context.startService(starts);
            }
        }
    }
}
