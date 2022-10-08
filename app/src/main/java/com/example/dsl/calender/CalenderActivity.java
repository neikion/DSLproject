package com.example.dsl.calender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsl.DSLUtil;
import com.example.dsl.calender.Calender.*;
import com.example.dsl.DSLManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.dsl.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CalenderActivity extends AppCompatActivity {//롱클릭으로 수정 삭제 진행
    //CalenderDataRepository repository = new Repository();//넌 폐기야
    List<Calender> calenderList;
    LinearLayout textViews;
    CalendarView calView;
    User user = User.getUserInstance();
    int userID = user.getUserCode();//통합환경에서는 유저의 상태를 가지고 있는 무언가가 있기를


    @Override
    protected void onStart() {//화면이 실행 될때마다 보여지는 녀석 서버로 조회기능 -> 리스트 업데이트 -> 업데이트된 리스트 기반으로 일정 재구축 -> 택스트뷰에 리스너 생성
        super.onStart();
        getCalenderList();
        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {//날짜 변화 리스너 이 기능의 모든것
                //칼랜더리스트의 요소를 순회하여 날짜가 일치할경우 textView 를 만들어서 보여줄 예정
                for (int i = 0; i < calenderList.size(); i++) {
                    DSLUtil.print(year+" "+month+" "+day);
                    if(calenderList.get(i).getScheduleYear() == year &&
                            calenderList.get(i).getScheduleMonth() == month+1 &&
                            calenderList.get(i).getScheduleDay() == day){
                        //여기는 리스트에서 현재 선택된 날짜와 동일한 녀석만 넘어올수 있음
                        Calender cal = calenderList.get(i);
                        TextView textView = createTextView(cal.getTitle());

                        textView.setOnClickListener(new View.OnClickListener() {//숏클릭 리스너 터치시 상세 페이지로 넘어간다 돌아오면 다시 onStart가 실행되어 재구축 예정 성능 ? 망 : 망
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CalenderActivity.this, schedule_content_viewer.class);
                                intent.putExtra("scheduleContent",cal);//직렬화를 해야 사용자 정의 클래스를 넣을수 있음
                                startActivity(intent);//액티비티 실행 이 인텐트는 일정 상세 페이지로 넘어간다 이파트는 대화상자로 변경의 가능성이 있음
                            }
                        });
                        textView.setOnLongClickListener(new View.OnLongClickListener() {//롱클릭 리스너 수정과 삭제가 가능하게 만든다 다만 여기서는 수행하지 않고 던질 예정
                            @Override
                            public boolean onLongClick(View view) {
                                String str;
                                AlertDialog.Builder dlg = new AlertDialog.Builder(getApplicationContext());
                                dlg.setTitle("일정 편집");

                                dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {//수정은 여기서 처리하지 않는다 던진다
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (cal.getUserCode() == -1) {
                                            Toast.makeText(getApplicationContext(),"자신의 일정만 수정할수 있습니다",Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        Intent intent = new Intent(getApplicationContext(), schedule_input.class);
                                        intent.putExtra("updateSchedule",cal);
                                        startActivity(intent);
                                        getCalenderList();
                                    }
                                });

                                dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {//업데이트는 던졌지만 이녀석은 던질수 없다 여기서 처리한다
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (cal.getUserCode() == -1) {
                                            Toast.makeText(getApplicationContext(),"자신의 일정만 삭제할수 있습니다",Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        try {
                                            DSLManager manager=DSLManager.getInstance();
                                            JSONObject data=new JSONObject();
                                            data.put("scheduleID",cal.getScheduleID());
                                            data.put("userCode",cal.getUserCode());
                                            manager.sendRequest(getApplicationContext(), data,"/calender/delete", new DSLManager.NetListener() {
                                                @Override
                                                public void Result(JSONArray Result) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        getCalenderList();
                                    }
                                });
                                return false;
                            }
                        });
                        textViews.addView(textView);
                        //end if
                    }
                    //end for loop
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {//최초 동작 메서드 안씀
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        calView = (CalendarView) findViewById(R.id.calender_view);
        textViews = (LinearLayout) findViewById(R.id.textViewLayout);
     //end main method
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {//메뉴 화면 생성기
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_calender,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//메뉴 를 사용하여서 add intent
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.insert:
                Intent intent = new Intent(getApplicationContext(), schedule_input.class);
                startActivity(intent);
                getCalenderList();
        }
        return false;
    }

    void getCalenderList() {//여기서 조회기능을 구현 해당 클래스의 전역공간의 calenderList 를 업데이트 하여준다
        ObjectMapper objectMapper = new ObjectMapper();
        List<Calender> list = new ArrayList<>();

//멀티쓰레드
        try {
            DSLManager manager=DSLManager.getInstance();
            JSONObject data=new JSONObject();
            data.put("userCode",9999);
            manager.sendRequest(getApplicationContext(), data,"/calender/select", new DSLManager.NetListener() {
                @Override
                public void Result(JSONArray Result) {
                    DSLUtil.print(Result.toString()+"");
                    try{
                        for (int i = 0; i < Result.length(); i++) {
                            JSONObject jj = Result.getJSONObject(i);
                            Calender cal = new Calender(jj.getInt("userCode"),jj.getInt("scheduleYear"),jj.getInt("scheduleMonth"),jj.getInt("scheduleDay"),jj.getString("title"),jj.getString("scheduleContent"),jj.getInt("scheduleID"));
                            DSLUtil.print(cal.toString());
                            list.add(cal);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }


        this.calenderList = list;
    }

    TextView createTextView(String title) {//동적으로 생성되는 텍스트뷰를 생성하여 리턴하여 주는 메서드 아름다움을 추구한다면 수정 해야지
        TextView textView = new TextView(getApplicationContext());
        textView.setText(title);
        textView.setTextSize(15);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        return textView;
    }


}