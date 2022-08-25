package com.example.dsl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DSLManager.NetListener {

    Button startbtn;
    TextView contenttv;
    Button endbtn;
    DSLManager manager;

    public void print(String s){
        Log.i("DSL",s);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //getInstance()로 manager객체를 가져옵니다.
        //manager=DSLManager.getInstance();

        init();

    }
    public void description(){
        try {
            //Read
            //필수 속성 Request, Column
            //검색 방법 1
            //testtable 테이블의 전체 값을 JSONArray로 읽어옵니다.
            print(manager.Reqeust("testtable").Column("*").Read(this).toString());

            //Read2
            //검색 방법 2
            //testtable 테이블의 모든 id를 JSONArray로 읽어옵니다.
            print(manager.Reqeust("testtable").Column("id").Read(this).toString());


            //Read with condition
            //조건있는 검색 방법 1
            //testtable 테이블 중 id의 값이 1인 행만 JSONArray로 읽어옵니다.
            print(manager.Reqeust("testtable").Column("*").Constraint("id","1").Read(this).toString());

            //Read with condition2
            //조건있는 검색 방법 2
            //testtable 테이블 중 id의 값이 1인 행의 id만 JSONArray로 읽어옵니다.
            print(manager.Reqeust("testtable").Column("id").Constraint("id","1").Read(this).toString());



            //Insert
            //필수 속성 Reqeust, Insert
            //행 생성 방법 1
            //testtable 테이블에 첫번째 열의 값이 1, 두 번째 열의 값이 value1, 세 번째 열의 값이 value2인 행을 생성합니다.
            String Create_Success=manager.Reqeust("testtable").Insert(new String[]{"1","'value1'","'value2'"}).Create(this);
            print(Create_Success);
            if(Create_Success.equals("200")){

                //Update succeed
            }


            //Update
            //필수 속성 Reqeust, Column, UpdateValue
            //조건 없는 업데이트 방법 1
            //testtable 테이블에서 data1 열의 모든 값을 data10으로 바꿉니다.
            String Update_Success1=manager.Reqeust("testtable").Column("data1").UpdateValue("data9").Update(this);
            print(Update_Success1);
            if(Update_Success1.equals("200")){
                //Update succeed
            }


            //Update with condition
            //조건있는 업데이트 방법 2
            //testtable 테이블에서 data1 열 중 id가 3인 객체를 data10으로 바꿉니다.
            String Update_Success2=manager.Reqeust("testtable").Column("data2").Constraint("id","1").UpdateValue("data10").Update(this);
            print(Update_Success2);
            if(Update_Success2.equals("200")){
                //Update succeed
            }


            //Delete
            //필수 속성 Reqeust
            //조건없는 삭제 방법
            //testtable 테이블의 전체 데이터 삭제
            String Delete_Success1=manager.Reqeust("testtable").Delete(this);

            //Delete with condition
            //조건있는 삭제 방법
            //testtable 테이블의 data1의 값이 data9인 행 삭제
            String Delete_Success2= manager.Reqeust("testtable").Constraint("data1","data9").Delete(this);


            //기타 설명
            //ServerReqeust를 객체를 만들어서 재사용을 하거나, 각 조건을 따로 입력할 수 있습니다.
            DSLManager.ServerReqeust request=manager.Reqeust("testtable");
            request.Column("*");
            request.Read(this);
            request.Read(this);

            //아레와 같이 두번 이상 속성을 부여하면 오류를 발생합니다.
            //manager.Reqeust("testtable2").Column("id").Column("data1").Read(this);
            //manager.Reqeust("testtable2").Constraint("id","1").Constraint("data1","10").Read(this);

            //필수 속성이 없다면 오류를 발생합니다.
            //manager.Reqeust("testtable2").Read(this);
            //manager.Reqeust("testtable2").Update(this);


            //아레 함수는 동기화 함수입니다.
            //Create(Context context);
            //Read(Context context);
            //Update(Context context);
            //Delete(Context context);

            //비동기 함수는 각 Read, Update, Insert, Delete함수에 추가적으로 NetListener를 지정해주시면 됩니다.
            //새로운 NetListener를 만드는 방법
            manager.Reqeust("testtable").Column("*").Read(this, new DSLManager.NetListener() {
                @Override
                public void Result(JSONArray Result) {
                    //Result로 객체가 반환됩니다.
                    System.out.println(Result.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                contenttv.setText(manager.Reqeust("testtable2").Column("*").Read(this).toString());
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
    }
}