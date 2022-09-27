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
    AlarmScheduler alarmSchedulernew = new AlarmScheduler(7);;
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
        findViewById(R.id.testactivity).setOnClickListener(this);
        table=new TimeTable(this,BaseTablePosition,UITablePosition,9);
        /*//noti 정상 작동하나 트래픽을 위해 테스트에서는 비활성화
        connect();*/
    }
    public void connect(){
        manager=DSLManager.getInstance();
        try {
            manager.sendRequest(this,DSLUtil.getTimeTable(),"/DB.jsp",(Result) -> {
                try{
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
        Intent intent=new Intent(this,MainActivity.class);
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
        //todo 알람 종류 선택가능하도록 하기
        //todo 알림 화면 만들기
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent=new Intent(this,TimeScheduleAlarmReceiver.class);
        intent.putExtra("AP",true);
//        intent.putExtra("Player", (Parcelable) MediaPlayer.create(this,R.raw.music));
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_IMMUTABLE);
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND,15);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),Alarmintent);
/*        AlarmManager.AlarmClockInfo info=new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),FullScreenIntent());
        alarmmanager.setAlarmClock(info,Alarmintent);
        Calendar d=Calendar.getInstance();
        d.setTimeInMillis(info.getTriggerTime());
        print(d.getTime());*/
        Log.i("DSL","\n\n 현재시각 "+ new Date(System.currentTimeMillis())+"\n 알람 예약 시간 "+new Date(c.getTimeInMillis()));


                //noti it is alarm test code it's right work
                new Handler().postDelayed(()->{
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(getApplicationContext(), TimeScheduleAlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(), 1, myIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

                    alarmManager.cancel(pendingIntent);
                },10000);
    }
    public void AlarmCheck(int requestCode){
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this,TimeScheduleAlarmReceiver.class);
        intent.putExtra("AP",true);
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,requestCode,intent,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_NO_CREATE);
        if(Alarmintent!=null){
            print("이미 있음");

        }else{
            print("없음");
        }
    }
    public void setAlarm(int day,int hour, int minute,int requestCode){
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this,TimeScheduleAlarmReceiver.class);
        intent.putExtra("AP",true);
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,requestCode,intent,PendingIntent.FLAG_IMMUTABLE);
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        //day == 0 is monday
        c.set(Calendar.DAY_OF_WEEK,((day+1)%7)+1);
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,0);
//        alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),1000*60*24*7,Alarmintent);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),Alarmintent);
        Log.i("DSL","\n\n 현재시각 "+ new Date(System.currentTimeMillis())+"\n 알람 예약 시간 "+new Date(c.getTimeInMillis()));
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
        } else if (id == R.id.testactivity) {
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            connect();
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
                //todo 넘어온 시간에 맞춰 알람 설정
            }else if(result.getResultCode()==Activity.RESULT_CANCELED){
                print("RESULT_CANCELED");
            }
        }
    });

    private void setTableChange(ArrayList<AdaptorDataSet> list){
        stickers=list;
        table.ChangeListener(stickers);
        setStickerOnClick();
    }
}