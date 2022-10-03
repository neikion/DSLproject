package com.example.dsl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SoftwareMajorSubjectActivity extends AppCompatActivity {

    ImageButton btnPrevious;
    Button btnRegist;
    Button btnDelete;
    ListView listSubject;

    // 과목 리스트용 어댑터
    ArrayList<NoticeItem> subjectItems = null;
    NoticeAdapter subjectAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_major_subject);

        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnRegist = (Button) findViewById(R.id.btnRegist);
        btnDelete = (Button) findViewById(R.id.btnCancel);
        listSubject = (ListView) findViewById(R.id.listSubject);

        btnRegist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SubjectAddActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), DataManagementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubjectRemoveActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        listSubject = (ListView) findViewById(R.id.listSubject);
        subjectItems = new ArrayList<NoticeItem>();
        subjectAdapter = new NoticeAdapter(this, subjectItems);
        listSubject.setAdapter(subjectAdapter);

        // 과목 읽어오기 Task 실행
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", -1);
        } catch (JSONException e) {
        }
        DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Notice/Subject/Search", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    subjectItems.clear();
                    NoticeItem item = new NoticeItem(-1, "전체보기");
                    subjectAdapter.add(item);
                    try {
                        for (int i = 0; i < Result.length(); i++) {
                            JSONObject jsonObject = Result.getJSONObject(i);
                            int ID = jsonObject.getInt("ID");
                            String Name = jsonObject.getString("Name");
                            item = new NoticeItem(ID, Name);
                            subjectAdapter.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        listSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }
}