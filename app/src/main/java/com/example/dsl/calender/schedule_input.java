package com.example.dsl.calender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.example.dsl.calender.Calender.*;
import com.example.dsl.DSLManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;

public class schedule_input extends AppCompatActivity {
    CalendarView calenderView;
    EditText editTitle,editContent;
    Button submitButton;
    int year,month,day;
    String title;
    String content;
    LocalDate now = LocalDate.now();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_input);
        Intent intent = getIntent();
        Calender intentCalender = (Calender) intent.getSerializableExtra("updateSchedule");
        editTitle = (EditText) findViewById(R.id.title_input);
        editContent = (EditText) findViewById(R.id.content_input);
        submitButton = (Button) findViewById(R.id.submit);
        calenderView = (CalendarView) findViewById(R.id.date_picker);
        year = now.getYear();
        month = now.getMonthValue() -  1;
        day = now.getDayOfMonth();
        calenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int methodYear, int methodMonth, int methodDay) {
                year = methodYear;month = methodMonth;day = methodDay;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = editTitle.getText().toString();
                content = editContent.getText().toString();
                Calender cal = null;
                if (intentCalender == null) {//insert
                    sendServer("insert",new Calender(DSLManager.getInstance().getUserCode(), year,month,day,title,content,0));
                }
                else{//update
                    sendServer("update",new Calender(DSLManager.getInstance().getUserCode(), year,month,day,title,content,intentCalender.getScheduleID()));
                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    private void sendServer(String option,Calender cal) {
        try {
            DSLManager manager=DSLManager.getInstance();
            JSONObject data=new JSONObject();
            data.put("scheduleID",cal.getScheduleID());
            data.put("userCode",DSLManager.getInstance().getUserCode());
            data.put("scheduleYear", cal.getScheduleYear());
            data.put("scheduleMonth", cal.getScheduleMonth() + 1);
            data.put("scheduleDay", cal.getScheduleDay());
            data.put("title", cal.getTitle());
            data.put("scheduleContent", cal.getScheduleContent());
            manager.sendRequest(getApplicationContext(), data,"/calender/" + option, new DSLManager.NetListener() {
                @Override
                public void Result(JSONArray Result) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}