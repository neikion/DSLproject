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
import java.util.Date;

public class TimeScheduleAlarmReceiver extends BroadcastReceiver {
    MediaPlayer player;
    Vibrator vibrator;
    VibrationEffect vibrationEffect;
    long[] vibratorPatten=new long[]{2000,2000,2000,2000};
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DSL","GetReceive : "+new Date(System.currentTimeMillis()));
        if(intent.getBooleanExtra("AP",false)){
            Intent starts=new Intent(context, TimeScheduleAlarmService.class);
            NotificationCompat.Builder noti=CreateNoti(context);
            starts.putExtra("AP",true);
            context.startService(starts);
        }else{
            Intent starts=new Intent(context, TimeScheduleAlarmService.class);
            starts.putExtra("AP",false);
            context.startService(starts);
        }


    }
    public NotificationCompat.Builder CreateNoti(Context context){
        NotificationCompat.Builder noti=new NotificationCompat.Builder(context,"TSNC");
        noti.setContentTitle("Alarm Title");
        noti.setContentText("알람을 중지하려면 여기를 터치하세요.");
        noti.setSmallIcon(R.drawable.ic_launcher_foreground);
        noti.setFullScreenIntent(ContentIntent(context),true);
        noti.setCategory(NotificationCompat.CATEGORY_ALARM);
        noti.setContentIntent(ServiceDownIntent(context));
        noti.setDeleteIntent(ServiceDownIntent(context));
        NotificationManager notimanager=context.getSystemService(NotificationManager.class);
        notimanager.notify(2,noti.build());
        return noti;
    }
    public PendingIntent ContentIntent(Context context){
        Intent i=new Intent(context,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending=PendingIntent.getActivity(context,2,i,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
    public PendingIntent ServiceDownIntent(Context context){
        Intent i=new Intent(context,TimeScheduleAlarmReceiver.class);
        i.putExtra("AP",false);
        PendingIntent pending=PendingIntent.getBroadcast(context,7,i,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
}
