package com.example.dsl;

import static com.example.dsl.DSLUtil.print;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Schedule extends AppCompatActivity implements View.OnClickListener {

    LinearLayout BaseTablePosition;
    LinearLayout UITablePosition;
    int BaseTableID;
    NotificationCompat.Builder noti;
    NotificationManager notimanager;
    private AlarmManager alarmmanager;
    private List<PendingIntent> alarmList=new LinkedList<>();
    TimeTable table;
    ArrayList<AdaptorDataSet> stickers;
    DSLManager manager;
    AlarmScheduler alarmScheduler = new AlarmScheduler(7);
    //noti test
    final int UserId=9999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        init();
        initNoti();
    }
    public void init(){
        BaseTablePosition=findViewById(R.id.BaseTablePosition);
        UITablePosition=findViewById(R.id.UITablePosition);
        findViewById(R.id.AddAlarm).setOnClickListener(this);
        findViewById(R.id.movemenu).setOnClickListener(this);
        findViewById(R.id.gettest).setOnClickListener(this);
        findViewById(R.id.sendtest).setOnClickListener(this);
        table=new TimeTable(this,BaseTablePosition,UITablePosition,9);
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        //noti 정상 작동하나 테스트에서는 비활성화
        getServerData();
    }
    private void getServerData(){
        manager=DSLManager.getInstance();
        try {
            JSONObject json=new JSONObject();
            json.put("table","time_schedule");
            json.put("column","*");
            json.put("constraint","user_code");
            json.put("constraint_value",String.valueOf(UserId));
            json.put("type","Read");
            manager.sendRequest(this,DSLUtil.getTimeTableObject(json),"/DB.jsp",(Result) -> {
                try{
                    if(Result==null){
                        return;
                    }
                    ArrayList<AdaptorDataSet> datalist=new ArrayList<>();
                    AdaptorDataSet dataSet;
                    for(int i=0;i<Result.length();i++){
                        JSONObject temp=Result.getJSONObject(i);
                        dataSet=new AdaptorDataSet();
                        dataSet.subject=temp.getString("subject");
                        dataSet.professor=temp.getString("professor");
                        dataSet.place=temp.getString("room");
                        dataSet.day=Integer.parseInt(temp.getString("day"));
                        dataSet.start=Integer.parseInt(temp.getString("start_time"));
                        dataSet.end=Integer.parseInt(temp.getString("end_time"));
                        dataSet.setAlarmGroup(Integer.parseInt(temp.getString("alarm")));
                        datalist.add(dataSet);
                    }
                    datalist.add(0,new AdaptorDataSet());
                    datalist.add(new AdaptorDataSet());
                    runOnUiThread(()->{
                        setTableChange(datalist);
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setServerData(){
        manager=DSLManager.getInstance();
        if(stickers!=null&&stickers.size()<3){
            return;
        }
        try{
            JSONObject json=new JSONObject();
            json.put("table","time_schedule");
            json.put("constraint","user_code");
            json.put("constraint_value",String.valueOf(UserId));
            json.put("type","Delete");
            StringBuilder sb=new StringBuilder();
            AdaptorDataSet dataSet;
            manager.sendRequest(this,DSLUtil.getTimeTableObject(json),"/DB.jsp",null);
            for(int i=1;i<stickers.size()-1;i++){
                dataSet=stickers.get(i);
                json=new JSONObject();
                json.put("table","time_schedule");
                sb.append(i).append(',').append(UserId).append(',').append('"').append(dataSet.subject).append('"').append(',').append('"').append(dataSet.professor)
                        .append('"').append(',').append(dataSet.day).append(',').append(dataSet.start).append(',').append(dataSet.end).append(',').append('"')
                        .append(dataSet.place).append('"').append(',').append(dataSet.getAlarmGroup());
                json.put("insert_value",sb.toString());
                json.put("type","Create");
                print(sb.toString());
                manager.sendRequest(this,DSLUtil.getTimeTableObject(json),"/DB.jsp",null);
                sb.setLength(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void initNoti(){

        CreatenotiChannel();
    }
    /*알림*/
    public void Createnoti(){

        noti=new NotificationCompat.Builder(this,"TSNC");
        noti.setContentTitle("TSNC Title");
        noti.setContentText("TSNC Context");
        noti.setSmallIcon(R.drawable.ic_launcher_foreground);
        noti.setStyle(new NotificationCompat.BigTextStyle().bigText("TSNC Style Text"));
        noti.setFullScreenIntent(FullScreenIntent(),true);
        noti.setAutoCancel(true);
    }
    private void CreatenotiChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notimanager=getSystemService(NotificationManager.class);
            if(notimanager.getNotificationChannel("TSNC")!=null){
                notimanager.deleteNotificationChannel("TSNC");
            }
            CharSequence name=getString(R.string.ScheduleNotiChannelName);
            String description=getString(R.string.ScheduleNotiChannelDescription);
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TSNC",name,importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            notimanager.createNotificationChannel(channel);


        }
    }
    public PendingIntent FullScreenIntent(){
        Intent intent=new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pending=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        return pending;
    }
    public void ShowNoti(){
        notimanager.notify(1,noti.build());
        try {
            Handler h=new Handler();
            h.postDelayed(()->{
                notimanager.cancel(1);
            },5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*알람*/
    public void initalarm(){
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        AdaptorDataSet a=new AdaptorDataSet();
        a.start=1822;
        a.professor="pp";
        a.end=a.start+100;
        a.subject="d";
        a.place="p";
        a.day=6;
        a.soundSwitch=true;
        a.vibrateSwitch=true;
        setAlarm(a);
    }
    public void setAlarm(AdaptorDataSet dataset){
        alarmScheduler.add(dataset.day,dataset.start);
        Intent sendintent=new Intent(this,TimeScheduleAlarmReceiver.class);
        sendintent.putExtra("AP",true);
        sendintent.putExtra("AlarmGroup",dataset.getAlarmGroup());
        sendintent.putExtra("Request_Code",alarmScheduler.get(dataset.day).get(dataset.start).request_code);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //day == 0 is monday
        calendar.set(Calendar.DAY_OF_WEEK,((dataset.day+1)%7)+1);
        calendar.set(Calendar.HOUR_OF_DAY,dataset.start/100);
        calendar.set(Calendar.MINUTE,dataset.start%100);
        calendar.set(Calendar.SECOND,0);
        sendintent.putExtra("DAY_OF_WEEK",calendar.get(Calendar.DAY_OF_WEEK));
        sendintent.putExtra("HOUR_OF_DAY",calendar.get(Calendar.HOUR_OF_DAY));
        sendintent.putExtra("MINUTE",calendar.get(Calendar.MINUTE));
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,alarmScheduler.get(dataset.day).get(dataset.start).request_code,sendintent,PendingIntent.FLAG_IMMUTABLE);
//        alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),7*24*60*60*1000,Alarmintent);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),Alarmintent);
        Log.i("DSL","\n\n 현재시각 "+ new Date(System.currentTimeMillis())+"\n 알람 예약 시간 "+new Date(calendar.getTimeInMillis()));
    }
    private void setStickerOnClick(){
        for(int i=table.TableColCount+table.TableRowCount-2;i<table.UITable.getChildCount();i++){
            int setid = i-(table.TableColCount+table.TableRowCount-2);
            ((TextView)table.UITable.getChildAt(i)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent go=new Intent(getApplicationContext(),ts_add.class);
                    if(stickers!=null){
                        go.putExtra("LegacySticker",stickers);
                        go.putExtra("addLegacySticker",setid);
                    }
                    ActivityLuncher.launch(go);
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.AddAlarm) {
            Intent i = new Intent(this, ts_add.class);
            if (stickers != null) {
                i.putExtra("LegacySticker", stickers);
            }
            ActivityLuncher.launch(i);
        } else if (id == R.id.movemenu) {
            initalarm();
        } else if (id == R.id.gettest) {
            getServerData();
//            startActivity(new Intent(this,AlarmActivity.class));
        }else if(id==R.id.sendtest){
            setServerData();
        }
    }


    ActivityResultLauncher<Intent> ActivityLuncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
//            print("onActivityResult");
            if(result.getResultCode()== Activity.RESULT_OK){
//                print("RESULT_OK");
                Intent ReIntent=result.getData();
                setTableChange((ArrayList<AdaptorDataSet>)ReIntent.getSerializableExtra("Result_Value"));
            }else if(result.getResultCode()==Activity.RESULT_CANCELED){
                print("RESULT_CANCELED");
            }
        }
    });

    private void setTableChange(ArrayList<AdaptorDataSet> list){
        stickers=list;
        table.ChangeListener(stickers);
        setStickerOnClick();
        clearAlarm();
        if(list.size()>2){
            for(int i=1;i<list.size()-1;i++){
                setAlarm(list.get(i));
            }
        }
//        setServerData();

    }
    public void clearAlarm(){
        Intent sendintent=new Intent(this,TimeScheduleAlarmReceiver.class);
        PendingIntent Alarmintent;
        for(int i=0;i<alarmScheduler.size();i++){
            AlarmScheduler.AlarmArray alarmArrays= alarmScheduler.get(i);
            for(int foo=0;foo<alarmArrays.size();foo++){
                AlarmScheduler.Alarmdata alarmdata=alarmArrays.getIndex(foo);
                if(alarmdata==null){
                    print("alarm data null");
                    continue;
                }
                Alarmintent=PendingIntent.getBroadcast(this,alarmdata.request_code,sendintent,PendingIntent.FLAG_IMMUTABLE);
                if(Alarmintent==null){
                    print("alarmintentnull");
                    continue;
                }
                alarmmanager.cancel(Alarmintent);
                print("alarmcancle"+alarmdata.toString());
            }
            alarmArrays.clear();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}