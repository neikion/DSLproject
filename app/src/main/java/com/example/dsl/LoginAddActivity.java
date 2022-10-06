package com.example.dsl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginAddActivity extends AppCompatActivity {

    EditText editId;
    EditText editPassword;
    EditText editNickName;
    Button btnRegist;
    ImageButton btnPrev;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_add);

        editId = (EditText)findViewById(R.id.editId);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editNickName = (EditText)findViewById(R.id.editNickName);
        btnRegist = (Button)findViewById(R.id.btnRegist);
        btnPrev = (ImageButton)findViewById(R.id.btnPrev);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("알림");
        alertDialog.setMessage("성공적으로 등록되었습니다.");
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", editId.getText().toString());
                    jsonObject.put("Password", editPassword.getText().toString());
                    jsonObject.put("NickName", editNickName.getText().toString());
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject, "/User/Insert", new DSLManager.NetListener() {
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