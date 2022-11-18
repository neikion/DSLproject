package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText editId;
    EditText editPassword;
    Button btnLogin;
    Button btnAdd;
    String strUserJson;
    AlertDialog.Builder alertDialog;
    String anser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editId = (EditText)findViewById(R.id.editId);
        editPassword = (EditText)findViewById(R.id.editPassword);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("알림");
        alertDialog.setMessage("로그인에 실패하였습니다.");

        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", editId.getText().toString());
                    jsonObject.put("Password", editPassword.getText().toString());
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/User/Search", Result -> runOnUiThread(()->{
                    try {
                        JSONObject jsonObject1 = Result.getJSONObject(0);
                        String ResultString = jsonObject1.getString("result");
                        if(ResultString.equals("OK")){
                            DSLManager.getInstance().Login(jsonObject1.getInt("userCode"),jsonObject1.getInt("grade"));
                            Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            alertDialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));

            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginAddActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}