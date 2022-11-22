package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.dsl.DSLManager;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginAddActivity extends AppCompatActivity {

    EditText editId;
    EditText editPassword;
    EditText editPasswordCheck;
    Button btnRegist;
    ImageButton btnPrev;
    AlertDialog.Builder alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_add);

        editId = findViewById(R.id.editId);
        editPassword = findViewById(R.id.editPassword);
        editPasswordCheck = findViewById(R.id.editPasswordCheck);
        btnRegist = findViewById(R.id.btnRegist);
        btnPrev = findViewById(R.id.btnPrev);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("알림");
        btnPrev.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        btnRegist.setOnClickListener(v -> {
            if(editPassword.getText().toString().equals(editPasswordCheck.getText().toString())){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", editId.getText().toString());
                    jsonObject.put("Password", editPassword.getText().toString());
//                    jsonObject.put("NickName", editNickName.getText().toString());
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/User/Insert", Result -> runOnUiThread(()->{
                    try {
                        JSONObject jsonObject1 = Result.getJSONObject(0);
                        String ResultString = jsonObject1.getString("result");
                        if(ResultString.equals("OK")){
                            alertDialog.setMessage("성공적으로 등록되었습니다.");
                            alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                            alertDialog.show();
                        }else{
                            alertDialog.setTitle("알림").setMessage("중복되는 아이디가 있습니다.").setPositiveButton("확인",null).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
            }else{
                alertDialog.setMessage("비밀번호가 일치하지 않습니다.").setPositiveButton("확인",null).show();
            }

        });
    }
}