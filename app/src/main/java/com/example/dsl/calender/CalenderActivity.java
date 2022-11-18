package com.example.dsl.calender;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.dsl.MenuBaseActivity;
import com.example.dsl.MenuCase1;
import com.example.dsl.calender.Calender.*;
import com.example.dsl.DSLManager;
import com.example.dsl.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalenderActivity extends MenuBaseActivity {
    LinearLayout textViews;
    CalendarView calView;
    ScrollView scrollView;
    Button button;
    List<TextView> textLists = null;
    int textViewLength = 0;
    LocalDate now = LocalDate.now();
    static int year,month, day;
    List<Calender> calenderList;
    public CalenderActivity() {
        super(new MenuCase1(),R.id.calender_root);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCalenderList();
    }

    private void checkScheduleDayAndCreateTextView(int year, int month, int day,List<Calender> calenderList) {
        if (textLists != null) {
            textLists.forEach(textView -> textViews.removeView(textView));//
        }
        textLists = new ArrayList<>();
        textViewLength = 0;
        calenderList.stream()
                .filter(calender -> calender.getScheduleDay() == day && calender.getScheduleMonth() == month && calender.getScheduleYear() == year)
                .forEach(calender -> {
                    textLists.add(createTextView(calender.getScheduleMonth() + "월" + calender.getScheduleDay() + "일 " + calender.getTitle()));
                    textLists.get(textViewLength).setOnClickListener(v -> {
                        Intent intent = new Intent(CalenderActivity.this, schedule_content_viewer.class);
                        intent.putExtra("scheduleContent", calender);
                        startActivity(intent);
                    });
                    textViews.addView(textLists.get(textViewLength++));
                });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        scrollView = findViewById(R.id.scrollView);
        textViews = findViewById(R.id.textViewLayout);
        calView = (CalendarView) findViewById(R.id.calender_view);
        button = findViewById(R.id.btn_add_schedule);
        year = now.getYear();
        month = now.getMonthValue();
        day = now.getDayOfMonth();
        findViewById(R.id.calender_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.openDrawer(Gravity.LEFT);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), schedule_input.class);
                startActivity(intent);
            }
        });
        calView.setOnDateChangeListener((view, year, month, dayOfMonth) -> checkScheduleDayAndCreateTextView(year, month + 1, dayOfMonth, calenderList));
    }

    void getCalenderList() {//여기서 조회기능을 구현 해당 클래스의 전역공간의 calenderList 를 업데이트 하여준다
        List<Calender> list = new ArrayList<>();
        try {
            DSLManager manager=DSLManager.getInstance();
            JSONObject data=new JSONObject();
            data.put("userCode",DSLManager.getInstance().getUserCode());
            manager.sendRequest(getApplicationContext(), data,"/calender/select", new DSLManager.NetListener() {
                @Override
                public void Result(JSONArray Result) {
                    try{
                        for (int i = 0; i < Result.length(); i++) {
                            JSONObject jj = Result.getJSONObject(i);
                            Calender cal = new Calender(jj.getInt("userCode"),jj.getInt("scheduleYear"),jj.getInt("scheduleMonth"),jj.getInt("scheduleDay"),jj.getString("title"),jj.getString("scheduleContent"),jj.getInt("scheduleID"));
                            list.add(cal);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> {
                        checkScheduleDayAndCreateTextView(year,month,day,calenderList);
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.calenderList = list;
    }

    TextView createTextView(String title) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(title);
        textView.setTextSize(15);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        return textView;
    }

}
