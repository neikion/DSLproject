package com.example.dsl;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentWriteActivity extends AppCompatActivity {

    ImageButton btnPrevious;
    Spinner spinSubject;
    TextInputEditText editName;
    TextInputEditText editContent;
    Button btnRegist;

    // 과목 스피너용 어댑터
    ArrayList<NoticeItem> subjectItems = null;
    NoticeAdapter subjectAdapter = null;

    // 등록용변수
    int AddSubjectID = -1;

    // 메시지 상자
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_write);

        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        editName = (TextInputEditText) findViewById(R.id.editName);
        editContent = (TextInputEditText) findViewById(R.id.textContent);
        btnRegist = (Button) findViewById(R.id.btnRegist);

        spinSubject = (Spinner) findViewById(R.id.spinSubject);
        subjectItems = new ArrayList<NoticeItem>();
        subjectAdapter = new NoticeAdapter(this, subjectItems);
        spinSubject.setAdapter(subjectAdapter);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("알림");
        alertDialog.setMessage("성공적으로 등록되었습니다.");
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", -1);
        } catch (JSONException e) {
        }
        DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject,"/Notice/Subject/Search", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    subjectItems.clear();
                    try {
                        for (int i = 0; i < Result.length(); i++) {
                            JSONObject jsonObject = Result.getJSONObject(i);
                            int ID = jsonObject.getInt("ID");
                            String Name = jsonObject.getString("Name");
                            NoticeItem item = new NoticeItem(ID, Name);
                            subjectAdapter.add(item);
                        }
                        spinSubject.setSelection(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });


        spinSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NoticeItem item = subjectItems.get(position);
                AddSubjectID = item.ID;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("SubjectID", AddSubjectID);
                    jsonObject.put("Name", editName.getText().toString());
                    jsonObject.put("Content", editContent.getText().toString());
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Notice/Notice/Insert", new DSLManager.NetListener() {
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
    }
}