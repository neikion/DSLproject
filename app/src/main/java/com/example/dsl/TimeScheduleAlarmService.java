package com.example.dsl;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.List;

public class TimeScheduleAlarmService extends Service {

    MediaPlayer player;
    NotificationCompat.Builder noti;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.i("DSL","onCreate");
        Createnoti();
        initPlayer();
    }
    public void initPlayer(){
        player=MediaPlayer.create(this,R.raw.music);
    }
    public void Createnoti(){
        noti=new NotificationCompat.Builder(this,"TSNC");
        noti.setContentTitle("Alarm Title");
        noti.setContentText("알람을 중지하려면 여기를 터치하세요.");
        noti.setSmallIcon(R.drawable.ic_launcher_foreground);
        noti.setFullScreenIntent(ContentIntent(),true);
        noti.setCategory(NotificationCompat.CATEGORY_ALARM);
        noti.setContentIntent(ServiceDownIntent());
    }
    public PendingIntent ContentIntent(){
        Intent i=new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending=PendingIntent.getActivity(getApplicationContext(),2,i,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
    public PendingIntent ServiceDownIntent(){
        Intent i=new Intent(this,TimeScheduleAlarmReceiver.class);
        i.putExtra("AP",false);
        PendingIntent pending=PendingIntent.getBroadcast(getApplicationContext(),7,i,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DSL","onStartCommand");
        if(intent.getExtras().getBoolean("AP")){
            startForeground(2,noti.build());
            player.start();
        }else{
            ServiceStop();
        }
        return START_NOT_STICKY;
    }
    public void ServiceStop(){
        player.stop();
        player.release();
        player=null;
        stopSelf();
    }
    @Override
    public void onDestroy() {
        if(player!=null){
            player.stop();
            player.release();
            player=null;
        }
        super.onDestroy();
        Log.i("DSL","Service destroy");
    }
}
