package com.example.dsl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class TimeScheduleAlarmReceiver extends BroadcastReceiver {
    public PendingIntent ContentIntent(Context context){
        Intent i=new Intent(context, AlarmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending=PendingIntent.getActivity(context,2,i,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
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
