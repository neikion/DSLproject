package com.example.dsl.notice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.MenuBaseActivity;
import com.example.dsl.MenuCase1;
import com.example.dsl.MenuFrame;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeActivity extends MenuBaseActivity {

    ImageButton btnMenu;
    Spinner spinSubject;
    Spinner spinSearch;
    EditText editSearch;
    Button btnSearch;
    Button btnWrite;
    ListView listNotice;

    // 과목 스피너용 어댑터
    ArrayList<NoticeItem> subjectItems = null;
    NoticeAdapter subjectAdapter = null;

    // 공지사항 목록용 어댑터
    ArrayList<NoticeItem> noticeItems = null;
    NoticeAdapter noticeAdapter = null;

    // 검색용변수
    int SearchID = -1;
    int SearchSubjectID = -1;
    String SearchName = "";
    String SearchContent = "";

    public NoticeActivity() {
        super(new MenuCase1(),R.id.notice_root);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
/*
        if(savedInstanceState != null)
        {
            SearchID = savedInstanceState.getInt("ID");
            SearchSubjectID = savedInstanceState.getInt("SubjectID");
            SearchName = savedInstanceState.getString("Name");
            SearchContent = savedInstanceState.getString("Content");
        }
        System.out.println(SearchSubjectID);
*/
        editSearch = (EditText) findViewById(R.id.editSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnWrite = (Button) findViewById(R.id.btnWrite);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContentWriteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.openDrawer(Gravity.LEFT);
            }
        });

        spinSubject = (Spinner) findViewById(R.id.spinSubject);
        subjectItems = new ArrayList<NoticeItem>();
        subjectAdapter = new NoticeAdapter(this, subjectItems);
        spinSubject.setAdapter(subjectAdapter);

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
                //noti 서버에서 비동기 처리가 안되어서 강제 동기 처리
                JSONObject jsonObject2 = new JSONObject();
                try {
                    jsonObject2.put("Id", SearchID);
                    jsonObject2.put("SubjectId", SearchSubjectID);
                    jsonObject2.put("Name", SearchName);
                    jsonObject2.put("Content", SearchContent);
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject2, "/Notice/Search", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        runOnUiThread(()->{
                            noticeAdapter.clear();
                            try {
                                for (int i = 0; i < Result.length(); i++) {
                                    JSONObject jsonObject = Result.getJSONObject(i);
                                    int ID = jsonObject.getInt("ID");
                                    String Name = jsonObject.getString("Name");
                                    //System.out.format("%d : %s\n", ID, Name);
                                    NoticeItem item = new NoticeItem(ID, Name);
                                    noticeAdapter.add(item);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });

            }
        });
        spinSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NoticeItem item = subjectItems.get(position);
                SearchSubjectID = item.ID;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinSearch = (Spinner) findViewById(R.id.spinSearch);
        ArrayAdapter searchAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_search, android.R.layout.simple_spinner_item);
        searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSearch.setAdapter(searchAdapter);
        spinSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listNotice = (ListView) findViewById(R.id.listSubject);
        noticeItems = new ArrayList<NoticeItem>();
        noticeAdapter = new NoticeAdapter(this, noticeItems);
        listNotice.setAdapter(noticeAdapter);

        // 과목 읽어오기 Task 실행
        if(!editSearch.getText().toString().isEmpty()){
            if(spinSearch.getSelectedItem().toString().equals("제목")) {
                SearchName = editSearch.getText().toString();
                SearchContent = "";
            }
            else {
                SearchName = "";
                SearchContent = editSearch.getText().toString();
            }
        }
        else {
            SearchName = "";
            SearchContent = "";
        }
        //noti 서버에서 비동기로 보낼 시 처리가 안되서 비활성화
/*        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put("Id", SearchID);
            jsonObject2.put("SubjectId", SearchSubjectID);
            jsonObject2.put("Name", SearchName);
            jsonObject2.put("Content", SearchContent);
        } catch (JSONException e) {
        }
        DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject2, "/Notice/Search", new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    noticeAdapter.clear();
                    try {
                        for (int i = 0; i < Result.length(); i++) {
                            JSONObject jsonObject = Result.getJSONObject(i);
                            int ID = jsonObject.getInt("ID");
                            String Name = jsonObject.getString("Name");
                            NoticeItem item = new NoticeItem(ID, Name);
                            noticeAdapter.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });*/
        listNotice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeItem item = noticeItems.get(position);
                SearchID = item.ID;
                Intent intent = new Intent(getApplicationContext(), ContentViewActivity.class);
                intent.putExtra("ID", SearchID);
                intent.putExtra("SubjectID", SearchSubjectID);
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editSearch.getText().toString().isEmpty()){
                    if(spinSearch.getSelectedItem().toString().equals("제목")) {
                        SearchName = editSearch.getText().toString();
                        SearchContent = "";
                    }
                    else {
                        SearchName = "";
                        SearchContent = editSearch.getText().toString();
                    }
                }
                else {
                    SearchName = "";
                    SearchContent = "";
                }
                JSONObject jsonObject3 = new JSONObject();
                try {
                    jsonObject3.put("Id", SearchID);
                    jsonObject3.put("SubjectId", SearchSubjectID);
                    jsonObject3.put("Name", SearchName);
                    jsonObject3.put("Content", SearchContent);
                } catch (JSONException e) {
                }
                DSLManager.getInstance().sendRequest(getApplicationContext(), jsonObject3, "/Notice/Search", new DSLManager.NetListener() {
                    @Override
                    public void Result(JSONArray Result) {
                        runOnUiThread(()->{
                            noticeAdapter.clear();
                            try {
                                for (int i = 0; i < Result.length(); i++) {
                                    JSONObject jsonObject = Result.getJSONObject(i);
                                    int ID = jsonObject.getInt("ID");
                                    String Name = jsonObject.getString("Name");
                                    //System.out.format("%d : %s\n", ID, Name);
                                    NoticeItem item = new NoticeItem(ID, Name);
                                    noticeAdapter.add(item);
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
/*
    // onPause 직전에 호출되는 부분, Bundle에 상태를 저장 할 수 있다.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", SearchID);
        outState.putInt("SubjectID", SearchSubjectID);
        outState.putString("Name", SearchName);
        outState.putString("Content", SearchContent);
    }
*/
}
