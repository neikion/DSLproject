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

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.example.dsl.schedule.TimeScheduleAlarmReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class BusNotiService extends Service {
    List<BusDataSet> constraintList =Collections.synchronizedList(new ArrayList<>());
    List<BusDataSet> currentBusState;
    NotificationCompat.Builder notiBuilder;
    NotificationManager notimanager;
    AlarmManager alarmmanager;
    private boolean runService=false;
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
        super.onCreate();
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        notiBuilder=CreateNoti(this);
        notimanager=this.getSystemService(NotificationManager.class);
        DSLUtil.print("service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runable();
        //언바인딩 되면 바로 헤제되도록 설정

        return START_NOT_STICKY;
    }

    public void startAlarm(){
        if(!runService){
            runService=true;
            runable();
        }else{
            cancleAlarm();
            setAlarm();
        }
    }
    private ArrayList<BusDataSet> refineDatatoBusDataSet(JSONArray json) throws Exception{
        JSONObject data;
        ArrayList<BusDataSet> result=new ArrayList<>();
        BusDataSet Busdata;
        String datastr;
        int timeindex=-1;
        for(int i=0;i<json.length();i++){
            data=json.getJSONObject(i);
            Busdata=new BusDataSet();
            try {
                Busdata.BusName=data.getString("busRouteAbrv");
                datastr=data.getString("arrmsg1");
                timeindex=datastr.indexOf("분");
                if(timeindex!=-1){
                    datastr=datastr.substring(0,timeindex);
                    datastr=datastr.replaceAll("[^0-9]*","");
                    Busdata.time=Integer.parseInt(datastr);
                    result.add(Busdata);
                }else{
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    private void runable(){
        if(constraintList !=null&& constraintList.size()>0){
            //todo id need

            DSLManager.getInstance().sendRequest(this,null,"/getBusPosition",(Result)->{
                try {
                    setBusState(refineDatatoBusDataSet(Result.getJSONArray(0)));
                    StringBuffer sb=new StringBuffer();
                    BusDataSet BusState;
                    for(int i = 0; i< constraintList.size(); i++){

                        for(int i2=0;i2<currentBusState.size();i2++){
                            BusState=currentBusState.get(i2);
                            if(constraintList.get(i).BusName.equals(BusState.BusName)){
                                if(BusState.time>=0){
                                    sb.append(constraintList.get(i).BusName).append(" 버스가 ").append(BusState.time).append("분 후 도착합니다.").append("\n");
                                }
                            }
                        }
                    }
                    if(sb.toString().length()>0){
                        notiBuilder.setContentText(sb.toString());
                        notimanager.notify(1,notiBuilder.build());
                    }else{
                        DSLUtil.print("sb string"+sb.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }else{
            if(currentBusState==null){
                DSLUtil.print(currentBusState==null);
            }else{
                DSLUtil.print("currentBusState size"+currentBusState.size());
            }
        }
        setAlarm();
    }
    private void setAlarm(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND,30);
        Intent sendintent=new Intent(this, BusNotiService.class);
        PendingIntent Alarmintent=PendingIntent.getService(this,10,sendintent,PendingIntent.FLAG_IMMUTABLE);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),Alarmintent);
    }
    private void cancleAlarm(){
        Intent sendintent=new Intent(this, BusNotiService.class);
        PendingIntent Alarmintent=PendingIntent.getService(this,10,sendintent,PendingIntent.FLAG_IMMUTABLE);
        alarmmanager.cancel(Alarmintent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    public void addConstraintBusData(BusDataSet data){
        constraintList.add(data);
    }
    public void removeConstraintBusData(String name){
        for(int i = constraintList.size()-1; i>-1; i--){
            if(constraintList.get(i).BusName.equals(name)){
                constraintList.remove(i);
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
        noti.setContentIntent(PendingIntent.getActivity(context,10,new Intent(),PendingIntent.FLAG_IMMUTABLE));
        noti.setCategory(NotificationCompat.CATEGORY_ALARM);
        noti.setAutoCancel(true);
        return noti;
    }

    public void setStopService(){
        cancleAlarm();
    }
    public void closeService(){
        cancleAlarm();
        stopSelf();
    }
    @Override
    public void onDestroy() {
        DSLUtil.print("service onDestroy");
        super.onDestroy();
    }
}