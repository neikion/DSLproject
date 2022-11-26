package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentViewActivity extends AppCompatActivity {

    ImageButton imageButton1;
    TextView textName;
    TextView textSubject;
    TextView textContent;
    TextInputEditText editContent;
    Button btnModify;
    Button btnDelete;

    int SearchID = -1;
    int SearchSubjectID = -1;
    int UserGrade = -1;

    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("알림");
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        SearchID = intent.getIntExtra("ID", -1);
        SearchSubjectID = intent.getIntExtra("SubjectID", -1);
        UserGrade = intent.getIntExtra("Grade", 3);

        imageButton1 = (ImageButton) findViewById(R.id.btnPrevious);
        textName = (TextView) findViewById(R.id.editText);
        textSubject = (TextView) findViewById(R.id.textSubject);
        textContent = (TextView) findViewById(R.id.textContent);
        editContent = (TextInputEditText) findViewById(R.id.editContent);
        btnModify = (Button) findViewById(R.id.btnModify);
        btnDelete = (Button) findViewById(R.id.btnDelete2);

        // by cys
        if( UserGrade == 3 )
        {
            btnModify.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);
            editContent.setVisibility(View.INVISIBLE);
            textContent.setVisibility(View.VISIBLE);
            textContent.setMovementMethod(new ScrollingMovementMethod());
        }
        else
        {
            editContent.setVisibility(View.VISIBLE);
            textContent.setVisibility(View.INVISIBLE);
        }

        textContent.setMovementMethod(new ScrollingMovementMethod());

        imageButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내용 수정
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", SearchID);
                    jsonObject.put("Name", "");
                    jsonObject.put("Content", editContent.getText().toString());
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Notice/Modify", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        runOnUiThread(()->{
                            try {
                                DSLUtil.print(jsonObject);
                                JSONObject jsonObject = Result.getJSONObject(0);
                                String ResultString = jsonObject.getString("result");
                                if(ResultString.equals("OK")){
                                    alertDialog.setMessage("성공적으로 수정되었습니다.");
                                    alertDialog.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내용 삭제
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", SearchID);
                    jsonObject.put("SubjectId", SearchSubjectID);
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Notice/Delete", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        runOnUiThread(()->{
                            try {
                                JSONObject jsonObject = Result.getJSONObject(0);
                                String ResultString = jsonObject.getString("result");
                                if(ResultString.equals("OK")){
                                    alertDialog.setMessage("성공적으로 삭제되었습니다.");
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

        // 과목 읽어오기 Task 실행
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", SearchID);
            jsonObject.put("SubjectId", SearchSubjectID);
            jsonObject.put("Name", "");
            jsonObject.put("Content", "");
        } catch (JSONException e) {
        }
        DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Notice/Search", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    try {
                        JSONObject jsonObject = Result.getJSONObject(0);
                        String Name = jsonObject.getString("Name");
                        String SubjectName = jsonObject.getString("SubjectName");
                        String Content = jsonObject.getString("Content");
                        textName.setText(Name);
                        textSubject.setText(SubjectName);
                        if(UserGrade==3){
                            textContent.setText(Content);
                        }else{
                            editContent.setText(Content);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
