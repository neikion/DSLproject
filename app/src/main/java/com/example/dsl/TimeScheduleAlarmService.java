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
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.List;

public class TimeScheduleAlarmService extends Service {

    MediaPlayer player;
    Vibrator vibrator;
    VibrationEffect vibrationEffect;
    long[] vibratorPatten=new long[]{2000,2000,2000,2000};
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.i("DSL","onCreate");
        initPlayer();
        initVibrator();
    }
    public void initPlayer(){
        player=MediaPlayer.create(this,R.raw.music);
    }
    private void initVibrator(){
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrationEffect=VibrationEffect.createWaveform(vibratorPatten,0);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DSL","onStartCommand");
        if(intent.getExtras().getBoolean("AP")){
            player.start();
//            VibrationAttributes.USAGE_CLASS_ALARM
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                VibrationAttributes a=new VibrationAttributes.Builder().setUsage(VibrationAttributes.USAGE_CLASS_ALARM).build();
            }
            vibrator.vibrate(vibrationEffect);
        }else{
            ServiceStop();
        }
        return START_NOT_STICKY;
    }
    public void ServiceStop(){
        if(player!=null){
            player.stop();
            player.reset();
            player.release();
            player=null;
        }
        vibrator.cancel();
        stopSelf();
    }
    @Override
    public void onDestroy() {
        DSLUtil.print((player==null)+"");
        super.onDestroy();
        Log.i("DSL","Service destroy");
    }
}
