package com.example.dsl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DSLManager.NetListener {

    Button startbtn;
    TextView contenttv;
    Button endbtn;
    DSLManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //getInstance()로 manager객체를 가져옵니다.
        //manager=DSLManager.getInstance();

        init();

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("DSL","main onstop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("DSL","main onDestroy");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("DSL","main onPause");
    }
    public void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        startbtn = findViewById(R.id.startbt);
        endbtn=findViewById(R.id.endbt);
        contenttv = findViewById(R.id.context);
        startbtn.setOnClickListener(this);
        endbtn.setOnClickListener(this);
        findViewById(R.id.testbt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Hello World!!", Toast.LENGTH_SHORT).show();
            }
        });
        manager=DSLManager.getInstance();


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.startbt){
            try {
                manager=DSLManager.getInstance();
                JSONObject data=new JSONObject();
                data.put("testparameter1","time_schedule");
                data.put("testparameter2","*");
                manager.sendRequest(getApplicationContext(), data,"/Any_Path/Any_File.jsp", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        //Result로 결과값이 옵니다.
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }else if(v.getId()==R.id.endbt){
            startService(new Intent(this,TimeScheduleAlarmService.class).putExtra("AP",false));
            finish();

            /*android.os.Process.killProcess(android.os.Process.myPid());*/
        }
    }

    @Override
    public void Result(JSONArray Result) {
        runOnUiThread(()->{
            try {
                contenttv.setText(Result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            //manager.Close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}