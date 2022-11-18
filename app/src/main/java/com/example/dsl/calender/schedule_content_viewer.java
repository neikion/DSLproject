package com.example.dsl.calender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dsl.DSLManager;
import com.example.dsl.calender.Calender.*;
import com.example.dsl.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class schedule_content_viewer extends AppCompatActivity {//일저의 상세를 보여주는 페이지 사실 필요했을까 의문이 남는다ㅏ아아아아아아아ㅏ아아아아아아아
    Button update,delete;
    TextView title,content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_content_viewer);
        Intent intent = getIntent();
        Calender calender = (Calender) intent.getSerializableExtra("scheduleContent");
        title = (TextView) findViewById(R.id.title_shower);
        content = (TextView) findViewById(R.id.content_shower);
        update = (Button) findViewById(R.id.schedule_update);
        delete = (Button) findViewById(R.id.schedule_delete);

        title.setText(calender.getTitle());
        content.setText(calender.getScheduleContent());

        update.setOnClickListener(v -> {
            if (DSLManager.getInstance().getUserCode() != calender.getUserCode()) {
                Toast.makeText(getApplicationContext(),"자신의 일정만 수정할수 있습니다",Toast.LENGTH_LONG).show();
                return;
            }
            Intent modifyIntent = new Intent(getApplicationContext(),schedule_input.class);
            modifyIntent.putExtra("updateSchedule", calender);
            startActivity(modifyIntent);
            finish();
        });

        delete.setOnClickListener(v -> {
            if (DSLManager.getInstance().getUserCode() != calender.getUserCode()) {
                Toast.makeText(getApplicationContext(),"자신의 일정만 삭제할수 있습니다",Toast.LENGTH_LONG).show();
                return;
            }
            try {
                DSLManager manager=DSLManager.getInstance();
                JSONObject data=new JSONObject();
                data.put("scheduleID",calender.getScheduleID());
                data.put("userCode",calender.getUserCode());
                manager.sendRequest(getApplicationContext(), data,"/calender/delete", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        //Result로 결과값이 옵니다.
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        });

//        update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (DSLManager.getInstance().getUserCode() != calender.getUserCode()) {
//                    Toast.makeText(getApplicationContext(),"자신의 일정만 수정할수 있습니다",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                Intent intent = new Intent(getApplicationContext(),schedule_input.class);
//                intent.putExtra("updateSchedule", calender);
//                startActivity(intent);
//                finish();
//            }
//        });

//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (DSLManager.getInstance().getUserCode() != calender.getUserCode()) {
//                    Toast.makeText(getApplicationContext(),"자신의 일정만 삭제할수 있습니다",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                try {
//                    DSLManager manager=DSLManager.getInstance();
//                    JSONObject data=new JSONObject();
//                    data.put("scheduleID",calender.getScheduleID());
//                    data.put("userCode",calender.getUserCode());
//                    manager.sendRequest(getApplicationContext(), data,"/calender/delete", new DSLManager.NetListener() {
//                        @Override
//                        public void Result(JSONArray Result) {
//                            //Result로 결과값이 옵니다.
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                finish();
//            }
//        });

    }
}