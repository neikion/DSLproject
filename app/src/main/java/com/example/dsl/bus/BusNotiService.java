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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class BusNotiService extends Service {
    public class ConstraintStation{
        //arsid
        String stationId;
        List<BusDataSet> constraintList=new ArrayList<>();
        public int getBus(String busName){
            for(int i=0;i<constraintList.size();i++){
                if(constraintList.get(i).BusName.equals(busName)){
                    return i;
                }
            }
            return -1;
        }

        @Override
        public String toString() {
            String data="";
            for(int i=0;i<constraintList.size();i++){
                data+=constraintList.get(i).toString()+"  ";
            }
            return "ConstraintStation{" +
                    "stationId='" + stationId + '\'' +
                    ", constraintList=" + data +
                    '}';
        }
    }
    List<ConstraintStation> constraintStationList=Collections.synchronizedList(new ArrayList<>());
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
        runable2();
        //언바인딩 되면 바로 헤제되도록 설정
        return START_NOT_STICKY;
    }

    public void startAlarm(){
        if(!runService){
            runable2();
        }else{
            cancleAlarm();
            setAlarm();
        }
    }
    private String refineDatatoMinuet(String str){
        String result;
        int timeindex=str.indexOf("분");
        if(timeindex!=-1){
            result=str.substring(0,timeindex);
            return result.replaceAll("[^0-9]*","");
        }
        return "응답없음";
    }
    private void runable2(){
        runService=true;
        if(constraintStationList.size()>0){
            List ResultValue=Collections.synchronizedList(new ArrayList<String>());
            final int workingcount=constraintStationList.size();

            for(int i=0;i<constraintStationList.size();i++){
                ConstraintStation selectStation=constraintStationList.get(i);
                JSONObject json=new JSONObject();
                try {
                    json.put("Id",selectStation.stationId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DSLManager.getInstance().sendRequest(this, json, "/getBusPosition", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        ResultValue.add(Result.toString());
                        if(ResultValue.size()==workingcount){
                            resultrunable(ResultValue);
                        }
                    }
                });
            }
        }
    }
    private void resultrunable(List result){
        JSONArray json;
        String stationId;
        String selectBus;
        int SearchBus;
        StringBuilder sb=new StringBuilder();
        String busTime;
        try{
            //설정한 정류장 검색
            for(int conStationIndex=0;conStationIndex<constraintStationList.size();conStationIndex++){
                stationId=constraintStationList.get(conStationIndex).stationId;
                //현재 버스 상태 검색
                for(int i2=0;i2<result.size();i2++){
                    json=new JSONArray(result.get(i2).toString());
                    if(json.getJSONObject(0).getString("arsId").equals(stationId)){
                        //정류장 맞음
                        for(int i3=0;i3<json.length();i3++){
                            selectBus=json.getJSONObject(i3).getString("busRouteAbrv");
                            busTime=refineDatatoMinuet(json.getJSONObject(i3).getString("arrmsg1"));
                            if(!busTime.equals("응답없음")&&Integer.parseInt(busTime)<=4){
                                //시간이 맞음. 전체 리스트 검색 전 확인
                                SearchBus=constraintStationList.get(conStationIndex).getBus(selectBus);
                                if(SearchBus>-1){
                                    //설정한 버스가 있음
                                    sb.append(constraintStationList.get(conStationIndex).constraintList.get(SearchBus).AlarmName).append("\n").append(selectBus).append(" 버스가 ").append(busTime).append("분 후 도착합니다.\n");
                                }
                            }else{
                                //곧 도착 처리
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(sb.length()>0){
            notiBuilder.setContentText(sb.toString());
            NotificationCompat.BigTextStyle style=new NotificationCompat.BigTextStyle();
            style.bigText(sb.toString());
            notiBuilder.setStyle(style);
            notimanager.notify(1,notiBuilder.build());
        }else{
            //알림 울릴 것이 없음
        }
        setAlarm();
    }
    private void setAlarm(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE,4);
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
        for(int i=0;i<constraintStationList.size();i++){
            if(constraintStationList.get(i).stationId.equals(data.arsId)){
                constraintStationList.get(i).constraintList.add(data);
                return;
            }
        }
        ConstraintStation station=new ConstraintStation();
        station.stationId =data.arsId;
        station.constraintList.add(data);
        constraintStationList.add(station);
    }
    public void removeConstraintBusData(String statioinId,String busname){
        int searchId=-1;
        for(int i=0;i<constraintStationList.size();i++){
            if(constraintStationList.get(i).stationId.equals(statioinId)){
                searchId=constraintStationList.get(i).getBus(busname);
                if(searchId!=-1){
                    constraintStationList.get(i).constraintList.remove(searchId);
                }
                return;
            }
        }
    }
    public void removeConstraintStation(String stationId){
        for(int i=0;i<constraintStationList.size();i++){
            if(constraintStationList.get(i).stationId.equals(stationId)){
                constraintStationList.remove(i);
            }
        }
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