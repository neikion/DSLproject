package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dsl.DSLManager;
import com.example.dsl.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubjectAddActivity extends AppCompatActivity {

    TextInputEditText txtName;
    Button btnRegist;
    Button btnCancel;

    // 메시지 상자
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_add);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("알림");
        alertDialog.setMessage("성공적으로 등록되었습니다.");
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), SoftwareMajorSubjectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        txtName = (TextInputEditText) findViewById(R.id.editName);
        btnRegist = (Button) findViewById(R.id.btnRegist);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnRegist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Name", txtName.getText().toString());
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/Subject/Insert", new DSLManager.NetListener() {
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
    }
}