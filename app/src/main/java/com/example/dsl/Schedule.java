package com.example.dsl;

import static com.example.dsl.DSLUtil.print;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.net.URI;
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
    ArrayList<TSListAdaptor.AdaptorDataSet> stickers;
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
        //todo 데이터베이스에서 값 받아오기
        table=new TimeTable(this,BaseTablePosition,UITablePosition,9);
        findViewById(R.id.gomainactivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
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
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_IMMUTABLE);
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND,20);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),Alarmintent);
        Log.i("DSL","\n\n 현재시각 "+ new Date(System.currentTimeMillis())+"\n 알람 예약 시간 "+new Date(c.getTimeInMillis()));


/*                //noti it is alarm test code it's right work
                new Handler().postDelayed(()->{
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(getApplicationContext(), TimeScheduleAlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(), 1, myIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

                    alarmManager.cancel(pendingIntent);
                    print("dd");
                },10000);*/
    }

    private void setonclick(){
        for(int i=table.TableColCount+table.TableRowCount-2;i<table.UITable.getChildCount();i++){
            int setid = i-(table.TableColCount+table.TableRowCount-2);
            ((TextView)table.UITable.getChildAt(i)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent go=new Intent(getApplicationContext(),ts_add.class);
                    if(stickers!=null){
//                        TSListAdaptor.AdaptorDataSet set =stickers.get(setid);
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
        switch (v.getId()){
            case R.id.AddAlarm:
                Intent i=new Intent(this,ts_add.class);
                if(stickers!=null){
                    i.putExtra("LegacySticker",stickers);
                }
                ActivityLuncher.launch(i);
                break;
            case R.id.movemenu:
                initalarm();


                break;
        }
    }


    ActivityResultLauncher<Intent> ActivityLuncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
//            print("onActivityResult");
            if(result.getResultCode()== Activity.RESULT_OK){
//                print("RESULT_OK");
                Intent ReIntent=result.getData();
                stickers = (ArrayList<TSListAdaptor.AdaptorDataSet>)ReIntent.getSerializableExtra("Result_Value");
                table.ChangeListener(stickers);
                setonclick();
                //todo 넘어온 시간에 맞춰 알람 설정
                //todo 설정값에 따라 수업, 교수, 장소 표기해주기
            }else if(result.getResultCode()==Activity.RESULT_CANCELED){
                print("RESULT_CANCELED");
            }
        }
    });
}