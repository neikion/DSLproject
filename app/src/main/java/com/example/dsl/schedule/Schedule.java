package com.example.dsl.schedule;

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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.MenuBaseActivity;
import com.example.dsl.MenuCase1;
import com.example.dsl.MenuFrame;
import com.example.dsl.R;
import com.example.dsl.notice.AdaptorDataSet;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Schedule extends MenuBaseActivity implements View.OnClickListener {

    private LinearLayout BaseTablePosition;
    private LinearLayout UITablePosition;
    private AlarmManager alarmmanager;
    private TimeTable table;
    private ArrayList<AdaptorDataSet> stickers;
    private DSLManager manager;
    private AlarmScheduler alarmScheduler = new AlarmScheduler(7);

    public Schedule() {
        super(new MenuCase1(), R.id.schedule_root);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        manager=DSLManager.getInstance();
        findViewById(R.id.movemenu).setOnClickListener(this);
        init();
        CreatenotiChannel();
    }
    private void init(){
        BaseTablePosition=findViewById(R.id.BaseTablePosition);
        UITablePosition=findViewById(R.id.UITablePosition);
        findViewById(R.id.AddAlarm).setOnClickListener(this);
        findViewById(R.id.movemenu).setOnClickListener(this);
        table=new TimeTable(this,BaseTablePosition,UITablePosition,9);
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        getServerData();
    }
    private void getServerData(){
        manager=DSLManager.getInstance();
        try {
            JSONObject json=new JSONObject();
            json.put("userCode",DSLManager.getInstance().getUserCode());
            manager.sendRequest(this, json,"/timeschedule/search",(Result) -> {
                try{
                    DSLUtil.print(Result.toString());
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
                        dataSet.start=Integer.parseInt(temp.getString("startTime"));
                        dataSet.end=Integer.parseInt(temp.getString("endTime"));
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
        JSONObject json=new JSONObject();
        try{
            if(stickers!=null&&stickers.size()<3){
                json.put("userCode",DSLManager.getInstance().getUserCode());
                manager.sendRequest(this, json, "/timeschedule/delete",null);
                return;
            }
            json.put("userCode",DSLManager.getInstance().getUserCode());
            manager.sendRequest(this, json, "/timeschedule/delete", Result -> {
                AdaptorDataSet dataSet;
                for(int i=1;i<stickers.size()-1;i++){
                    dataSet=stickers.get(i);
                    JSONObject json1 =new JSONObject();
                    try {
                        json1.put("userCode",DSLManager.getInstance().getUserCode());
                        json1.put("subject",dataSet.subject);
                        json1.put("professor",dataSet.professor);
                        json1.put("day",dataSet.day);
                        json1.put("startTime",dataSet.start);
                        json1.put("endTime",dataSet.end);
                        json1.put("room",dataSet.place);
                        json1.put("alarm",dataSet.getAlarmGroup());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    manager.sendRequest(getApplicationContext(), json1, "/timeschedule/insert", null);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void CreatenotiChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManager notimanager=getSystemService(NotificationManager.class);
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
    public void setAlarm(AdaptorDataSet dataset){
        alarmScheduler.add(dataset.day,dataset.start);
        Intent sendintent=new Intent(this, TimeScheduleAlarmReceiver.class);
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

        Calendar calendar2=Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        if(calendar.compareTo(calendar2)<0){
            calendar.add(Calendar.DAY_OF_WEEK_IN_MONTH,1);
        }
        sendintent.putExtra("DAY_OF_WEEK",calendar.get(Calendar.DAY_OF_WEEK));
        sendintent.putExtra("HOUR_OF_DAY",calendar.get(Calendar.HOUR_OF_DAY));
        sendintent.putExtra("MINUTE",calendar.get(Calendar.MINUTE));

        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,alarmScheduler.get(dataset.day).get(dataset.start).request_code,sendintent,PendingIntent.FLAG_IMMUTABLE);
//        alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),7*24*60*60*1000,Alarmintent);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),Alarmintent);

        Log.i("DSL","\n\n 현재시각 "+ new Date(System.currentTimeMillis())+"\n 알람 예약 시간 "+new Date(calendar.getTimeInMillis()));
    }
    private void setStickerOnClick(){
        for(int i=table.getTableColCount()+table.getTableRowCount()-2;i<table.UITable.getChildCount();i++){
            int setid = i-(table.getTableColCount()+table.getTableRowCount()-2);
            ((TextView)table.UITable.getChildAt(i)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent go=new Intent(getApplicationContext(), ts_add.class);
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
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.AddAlarm) {
            Intent i = new Intent(this, ts_add.class);
            if (stickers != null) {
                i.putExtra("LegacySticker", stickers);
            }
            ActivityLuncher.launch(i);
        } else if (id == R.id.movemenu) {
            menuLayout.openDrawer(Gravity.LEFT);
        }
    }


    private ActivityResultLauncher<Intent> ActivityLuncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
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
        print(stickers.size()+"");
        setServerData();

    }
    private void clearAlarm(){
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
                print("alarmcancle"+alarmdata);
            }
            alarmArrays.clear();
        }
    }
}