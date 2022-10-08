package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dsl.R;
import com.example.dsl.calender.CalenderActivity;
import com.example.dsl.schedule.Schedule;
import com.example.dsl.weather.WeatherActivity;

public class MenuActivity extends AppCompatActivity {

    TextView txtMenuGroup1;
    Button btnSchedule;
    ImageButton btnPrevious;
    ImageButton btnConfig;
    Button btnLocation;
    TextView txtMenuGroup2;
    Button btnCalendar;
    Button btnWeather;
    Button btnTraffic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtMenuGroup1 = (TextView) findViewById(R.id.txtMenuGroup1);
        btnSchedule = (Button) findViewById(R.id.btnSchedule);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnConfig = (ImageButton) findViewById(R.id.btnConfig);
        btnLocation = (Button) findViewById(R.id.btnLocation);
        txtMenuGroup2 = (TextView) findViewById(R.id.txtMenuGroup2);
        btnCalendar = (Button) findViewById(R.id.btnCalendar);
        btnWeather = (Button) findViewById(R.id.btnWeather);
        btnTraffic = (Button) findViewById(R.id.btnTraffic);

        btnPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ConfMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), Schedule.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), WeatherActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), CalenderActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }
}