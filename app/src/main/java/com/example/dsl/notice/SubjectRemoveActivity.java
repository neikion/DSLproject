package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.dsl.DSLManager;
import com.example.dsl.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubjectRemoveActivity extends AppCompatActivity {

    TextInputEditText editName;
    Button btnDelete;
    Button btnCancel;
    ListView listSubject;

    // 과목 리스트용 어댑터
    ArrayList<NoticeItem> subjectItems = null;
    NoticeAdapter subjectAdapter = null;

    // 삭제용변수
    int DelSubjectID = -1;

    // 메시지 상자
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_remove);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("알림");
        alertDialog.setMessage("성공적으로 삭제되었습니다.");
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), SoftwareMajorSubjectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        editName = (TextInputEditText) findViewById(R.id.editName);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        listSubject = (ListView) findViewById(R.id.listSubject);

        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", DelSubjectID);
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Subject/Delete", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        runOnUiThread(()->{
                            try {
                                JSONObject jsonObject = Result.getJSONObject(0);
                                String ResultString = jsonObject.getString("result");
                                if(ResultString.equals("OK")){
                                    alertDialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SoftwareMajorSubjectActivity.class);
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
            jsonObject.put("Id", -1);
        } catch (JSONException e) {
        }
        DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Subject/Search", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    subjectItems.clear();
                    try {
                        for (int i = 0; i < Result.length(); i++) {
                            JSONObject jsonObject = Result.getJSONObject(i);
                            int ID = jsonObject.getInt("ID");
                            String Name = jsonObject.getString("Name");
                            //System.out.format("%d : %s\n", ID, Name);
                            NoticeItem item = new NoticeItem(ID, Name);
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
                NoticeItem item = subjectItems.get(position);
                DelSubjectID = item.ID;
                editName.setText(item.Name);
            }
        });
    }
}