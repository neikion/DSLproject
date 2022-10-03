package com.example.dsl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                Intent i=new Intent(getApplicationContext(),Schedule.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }
}