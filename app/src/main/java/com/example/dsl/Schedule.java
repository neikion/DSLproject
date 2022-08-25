package com.example.dsl;

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

public class Schedule extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener,View.OnClickListener {

    LinearLayout BaseTablePosition;
    LinearLayout UITablePosition;
    int TableWidth;
    int TableHeight;
    int Cell_Height=0;
    int Cell_Width=0;
    int TableColCount;
    int TableRowCount;
    int RowCellDivdeNum=7;
    int BaseTableID;
    int UITableID;
    GridLayout BaseTable;
    GridLayout UITable;
    NotificationCompat.Builder noti;
    NotificationManager notimanager;
    private AlarmManager alarmmanager;
    private List<PendingIntent> alarmList=new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        //it is Custom Auto Cell Height
        Cell_Height=-1;
        init(11,6);
        initNoti();
    }
    public void init(int rowcount,int colcount){
        TableRowCount=rowcount;
        TableColCount=colcount;
        BaseTablePosition=findViewById(R.id.BaseTablePosition);
        BaseTable=CreateGridLayout();
        BaseTablePosition.addView(BaseTable);
        UITablePosition=findViewById(R.id.UITablePosition);
        UITable=CreateGridLayout();
        UITablePosition.addView(UITable);
        UITable.getViewTreeObserver().addOnGlobalLayoutListener(this);
        findViewById(R.id.AddAlarm).setOnClickListener(this);
        findViewById(R.id.movemenu).setOnClickListener(this);
        findViewById(R.id.gomainactivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
    private GridLayout CreateGridLayout(){
        GridLayout grid=new GridLayout(this);
        grid.setColumnCount(TableColCount);
        grid.setRowCount(TableRowCount);
        GridLayout.LayoutParams gridParam=new GridLayout.LayoutParams();
        gridParam.height=GridLayout.LayoutParams.MATCH_PARENT;
        gridParam.width=GridLayout.LayoutParams.MATCH_PARENT;
        gridParam.setMargins(0,0,0,0);
        grid.setLayoutParams(gridParam);
        grid.setOrientation(GridLayout.VERTICAL);
        BaseTableID=View.generateViewId();
        grid.setId(BaseTableID);
        return grid;
    }
    final String[] dayarray=new String[]{"월요일","화요일","수요일","목요일","금요일","토요일","일요일"};
    private void UILayoutSetting(GridLayout grid){
        TextView v;
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new TextView(this);
                v.setPadding((int)DPtoPX(10),(int)DPtoPX(10),5,5);
                v.setBackground(getItemsBGWithoutBorder(getItemsRadius(rowc,colc)));
                if(rowc==0 && colc>0){
                    v.setText(dayarray[colc-1]);
                }
                if(rowc>0&&colc==0){
                    v.setText((rowc+8)+"");
                }
                ItemsAdd(v, grid, rowc, colc);

            }
        }
    }
    private void BaseLayoutSetting(GridLayout grid){
        View v;
        //cell setting init
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new View(this);
                v.setBackground(getItemsBGWithBorder(getItemsRadius(rowc,colc)));
                ItemsAdd(v, grid, rowc, colc);

            }
        }

        /*
        //특정 셀 편집 방법
        View items=grid.getChildAt(0);
        ((TextView)grid.getChildAt(9)).setText("dd");
        grid.removeViewAt(10);
        GridLayout.LayoutParams edit=new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED,2),GridLayout.spec(GridLayout.UNDEFINED,1));
        edit.height=Cell_Height*2;
        edit.width=Cell_Width;
        grid.getChildAt(9).setLayoutParams(edit);
        */
    }
    private void ItemsAdd(View Items,GridLayout grid,int rowcount,int colcount){
        GridLayout.Spec row,col;
        row=GridLayout.spec(rowcount,1);
        col=GridLayout.spec(colcount,1);
        GridLayout.LayoutParams gridChildparams=new GridLayout.LayoutParams(row,col);
        gridChildparams.width=Cell_Width;
        gridChildparams.height=Cell_Height;
        grid.addView(Items,gridChildparams);
    }
    private float[] getItemsRadius(int rowcount,int colcount){
        float[] radi=new float[8];

        if(colcount==0){
            if(rowcount==0){
                radi[0]=20;
                radi[1]=20;
            }else if(rowcount==TableRowCount-1){
                radi[6]=20;
                radi[7]=20;
            }
        }else if(colcount==TableColCount-1){
            if(rowcount==0){
                radi[2]=20;
                radi[3]=20;
            }else if(rowcount==TableRowCount-1){
                radi[4]=20;
                radi[5]=20;
            }
        }
        return radi;
    }
    public float DPtoPX(float dipValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    public GradientDrawable getItemsBGWithBorder(float[] radius) {
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setStroke((int)DPtoPX(1), Color.BLACK);
        gdDefault.setCornerRadii(radius);
        return gdDefault;
    }
    public GradientDrawable getItemsBGWithoutBorder(float[] radius){
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setCornerRadii(radius);
        return gdDefault;
    }
    //get GridLayout View Value
    @Override
    public void onGlobalLayout() {
        //if trigger not remove, it is called View any change
        BaseTable.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        TableHeight=BaseTable.getHeight();
        TableWidth=BaseTable.getWidth();
        if(Cell_Width==0){
            Cell_Width=TableWidth/TableColCount;
        }
        if(Cell_Height==0){
            Cell_Height=TableHeight/TableRowCount;
        }else if(Cell_Height==-1){
            Cell_Height=TableHeight/RowCellDivdeNum;
        }
        BaseLayoutSetting(BaseTable);
        UILayoutSetting(UITable);
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
        alarmmanager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this,TimeScheduleAlarmReceiver.class);
        intent.putExtra("AP",true);
        PendingIntent Alarmintent=PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_IMMUTABLE);
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND,5);
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),Alarmintent);
        Log.i("DSL","\n\n 현재시각 "+ new Date(System.currentTimeMillis())+"\n 알람 예약 시간 "+new Date(c.getTimeInMillis()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.AddAlarm:
                Intent i=new Intent(this,ts_add.class);
                i.putExtra("BaseTableID",BaseTableID);
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
            DSLUtil.print("onActivityResult");
            if(result.getResultCode()== Activity.RESULT_OK){
                Intent resultintent=result.getData();
                ((TextView)UITable.getChildAt(0)).setText("dd");
            }
        }
    });
}