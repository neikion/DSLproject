package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dsl.DSLManager;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentViewActivity extends AppCompatActivity {

    ImageButton imageButton1;
    TextView textName;
    TextView textSubject;
    TextView textContent;


    int SearchID = -1;
    int SearchSubjectID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);

        Intent intent = getIntent();
        SearchID = intent.getIntExtra("ID", -1);
        SearchSubjectID = intent.getIntExtra("SubjectID", -1);

        imageButton1 = (ImageButton) findViewById(R.id.btnPrevious);
        textName = (TextView) findViewById(R.id.textSubject);
        textSubject = (TextView) findViewById(R.id.editName);
        textContent = (TextView) findViewById(R.id.textContent);

        imageButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        // 과목 읽어오기 Task 실행
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", SearchID);
            jsonObject.put("SubjectID", SearchSubjectID);
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
                        textContent.setText(Content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}