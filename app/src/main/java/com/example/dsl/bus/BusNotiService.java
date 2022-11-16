package com.example.dsl.bus;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.example.dsl.schedule.AlarmActivity;
import com.example.dsl.schedule.TimeScheduleAlarmReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BusNotiService extends Service {
    List<BusDataSet> list=Collections.synchronizedList(new ArrayList<>());
    List<BusDataSet> currentBusState;
    NotificationCompat.Builder notiBuilder;
    NotificationManager notimanager;
    AlarmManager alarmmanager;
    public class BusNotiBinder extends Binder {
        public BusNotiService getService(){
            return BusNotiService.this;
        }
    }
    private IBinder myBinder=new BusNotiBinder();
    public BusNotiService() {
    }

    @Override
    public void onCreate() {
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        notiBuilder=CreateNoti(this);
        notimanager=this.getSystemService(NotificationManager.class);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void runable(){
        if(currentBusState!=null&&currentBusState.size()>0){
            DSLUtil.print(currentBusState.size());
            StringBuffer sb=new StringBuffer();
            try{
                BusDataSet BusState;
                for(int i=0;i<list.size();i++){
                    BusState=currentBusState.get(i);
                    if(list.get(i).BusName.equals(BusState.BusName)){
                        if(BusState.time>0){
                            sb.append(list.get(i).BusName).append(" 버스가 ").append(BusState.time).append("분 후 도착합니다.").append("\n");
                        }
                    }
                }
            }catch (Exception e){

            }
            if(sb.toString().length()>0){
                notiBuilder.setContentText(sb.toString());
                notimanager.notify(1,notiBuilder.build());
            }

        }
    }
    private void setAlarm(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE,1);
        Intent sendintent=new Intent(this, TimeScheduleAlarmReceiver.class);
        sendintent.putExtra("BusReplay",true);
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,10,sendintent,PendingIntent.FLAG_IMMUTABLE);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),Alarmintent);
    }
    private void cancleAlarm(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE,1);
        Intent sendintent=new Intent(this, TimeScheduleAlarmReceiver.class);
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,10,sendintent,PendingIntent.FLAG_IMMUTABLE);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),Alarmintent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    public void addConstraintBusData(String name){
        BusDataSet bus=new BusDataSet();
        bus.BusName=name;
        list.add(bus);
    }
    public void removeConstraintBusData(String name){
        for(int i=list.size()-1;i>-1;i--){
            if(list.get(i).BusName.equals(name)){
                list.remove(i);
            }
        }
    }
    public void setBusState(ArrayList<BusDataSet> currentBusState){
        this.currentBusState=Collections.synchronizedList(currentBusState);
    }
    private NotificationCompat.Builder CreateNoti(Context context){
        NotificationCompat.Builder noti=new NotificationCompat.Builder(context,"TSNC");
        noti.setContentTitle("버스가 곧 도착합니다.");
        noti.setSmallIcon(R.drawable.logo);
        noti.setContentIntent(PendingIntent.getActivity(context,1,new Intent(),PendingIntent.FLAG_IMMUTABLE));
        noti.setCategory(NotificationCompat.CATEGORY_ALARM);
        noti.setAutoCancel(true);
        return noti;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}