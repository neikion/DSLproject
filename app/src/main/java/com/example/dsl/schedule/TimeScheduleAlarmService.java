package com.example.dsl.schedule;

import static com.example.dsl.DSLUtil.print;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import java.util.Calendar;
import java.util.Date;

public class TimeScheduleAlarmService extends Service {

    MediaPlayer player;
    Vibrator vibrator;
    VibrationEffect vibrationEffect;
    long[] vibratorPatten=new long[]{0,2000,2000};
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
        player=MediaPlayer.create(this, R.raw.music);
        //todo audio foucus와 audio manager를 통해 볼륨 조절
        //noti 현재 볼륨은 사용자가 설정한 볼륨을 기반으로 재생됨
        player.setVolume(1,1);
    }
    private void initVibrator(){
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrationEffect=VibrationEffect.createWaveform(vibratorPatten,0);
    }
    public NotificationCompat.Builder CreateNoti(Context context,Intent intent){
        NotificationCompat.Builder noti=new NotificationCompat.Builder(context,"TSNC");
        noti.setContentTitle("Alarm Title");
        noti.setContentText("알람을 중지하려면 여기를 터치하세요.");
        noti.setSmallIcon(R.drawable.ic_launcher_foreground);
        noti.setFullScreenIntent(ContentIntent(context,intent.getIntExtra("HOUR_OF_DAY",0),intent.getIntExtra("MINUTE",0)),true);
        noti.setCategory(NotificationCompat.CATEGORY_ALARM);
        noti.setContentIntent(ServiceDownIntent(context));
        noti.setDeleteIntent(ServiceDownIntent(context));
        noti.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        return noti;
    }
    public PendingIntent ContentIntent(Context context,int Hour, int Minuet){
        Intent i=new Intent(context, AlarmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("HOUR_OF_DAY",Hour);
        i.putExtra("MINUTE",Minuet);
        PendingIntent pending=PendingIntent.getActivity(context,2,i,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
    public PendingIntent ServiceDownIntent(Context context){
        Intent i=new Intent(context,TimeScheduleAlarmReceiver.class);
        i.putExtra("AP",false);
        PendingIntent pending=PendingIntent.getBroadcast(context,7,i,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DSL","onStartCommand");
        if(intent.getExtras().getBoolean("AP")){
            int AlarmGroup=intent.getIntExtra("AlarmGroup",0);
            if(AlarmGroup<1){
                NotificationManager notimanager=this.getSystemService(NotificationManager.class);
                notimanager.notify(2,CreateNoti(this,intent).build());
                return START_NOT_STICKY;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(2,CreateNoti(getApplicationContext(),intent).build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            }else{
                startForeground(2,CreateNoti(getApplicationContext(),intent).build());
            }
            if(AlarmGroup!=2){
                vibrator.vibrate(vibrationEffect);
            }
            if(AlarmGroup>1){

                player.start();

            }
            setNextAlarm(intent);
        }else{
            ServiceStop();
        }
        return START_NOT_STICKY;
    }
    private void setNextAlarm(Intent intent){
        AlarmManager alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent sendintent=new Intent(this,TimeScheduleAlarmReceiver.class);
        sendintent.putExtra("AP",true);
        int AlarmGourp=intent.getIntExtra("AlarmGroup",0);
        int request_code=intent.getIntExtra("Request_Code",0);
        int day=intent.getIntExtra("DAY_OF_WEEK",0);
        int hour=intent.getIntExtra("HOUR_OF_DAY",0);
        int minute=intent.getIntExtra("MINUTE",0);

        Calendar calendar=Calendar.getInstance();
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,request_code,sendintent,PendingIntent.FLAG_IMMUTABLE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        //day == 0 is monday
        calendar.set(Calendar.DAY_OF_WEEK,day);
        calendar.add(Calendar.DAY_OF_WEEK,7);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        sendintent.putExtra("DAY_OF_WEEK",calendar.get(Calendar.DAY_OF_WEEK));
        sendintent.putExtra("HOUR_OF_DAY",calendar.get(Calendar.HOUR_OF_DAY));
        sendintent.putExtra("MINUTE",calendar.get(Calendar.MINUTE));
        DSLUtil.print("다음 알림"+new Date(calendar.getTimeInMillis()));
        DSLUtil.print(sendintent.getExtras().toString());
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),Alarmintent);

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
        super.onDestroy();
        Log.i("DSL","Service destroy");
    }
}
