package com.example.dsl.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.example.dsl.notice.MenuActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

public class WeatherActivity extends AppCompatActivity {

    TextView dateView;

    TextView cityView;
    TextView weatherView;
    TextView tempView;
    TextView mainView;
    TextView mainView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        dateView = findViewById(R.id.dateView);

        cityView = findViewById(R.id.cityView);
        weatherView = findViewById(R.id.weatherView);
        tempView = findViewById(R.id.tempView);
        mainView = findViewById(R.id.mainView);
        mainView2 = findViewById(R.id.mainView2);
        ImageButton button = findViewById(R.id.imageButton);
        findViewById(R.id.weather_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DSLManager.gomenu(getApplicationContext());
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //시간데이터와 날씨데이터 활용
                CurrentCall();
            }
        });
    }

    private void CurrentCall(){

        String url = "https://api.openweathermap.org/data/2.5/weather?q=Seongnam-si&appid=e7569e766bcbd4081766788d906cfe65";

        DSLManager.getInstance().sendRequestforWeather(this, url, new DSLManager.NetListener() {
            @Override
            public void Result(JSONArray Result) {
                runOnUiThread(()->{
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    //년, 월, 일 형식으로. 시,분,초 형식으로 객체화하여 String에 형식대로 넣음
                    SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
                    String getDay = simpleDateFormatDay.format(date);
                    String getTime = simpleDateFormatTime.format(date);

                    //getDate에 개행을 포함한 형식들을 넣은 후 dateView에 text설정
                    String getDate = getDay + "\n" + getTime;
                    dateView.setText(getDate);
                    try{
                        //api로 받은 파일 jsonobject로 새로운 객체 선언
                        JSONObject jsonObject = new JSONObject(Result.getJSONObject(0).toString());
                        JSONObject jsonObject2 = new JSONObject(Result.getJSONObject(0).toString());


                        //도시 키값 받기
                        String city = jsonObject.getString("name");

                        cityView.setText("현재위치 : " + city);



                        // String clouds = jsonObject.getString("clouds");

                        // cloudsView.setText("구름량 : " + clouds);





                        //날씨 키값 받기
                        JSONArray weatherJson = jsonObject.getJSONArray("weather");
                        JSONObject weatherObj = weatherJson.getJSONObject(0);

                        String weather = weatherObj.getString("description");

                        weatherView.setText("날씨 상황 : " + weather);






                        //기온 키값 받기
                        JSONObject tempK = new JSONObject(jsonObject.getString("main"));

                        //기온 받고 켈빈 온도를 섭씨 온도로 변경
                        double tempDo = (Math.round((tempK.getDouble("temp")-273.15)*100)/100.0);
                        tempView.setText(tempDo +  "°C");














                        JSONObject tempK1 = new JSONObject(jsonObject.getString("main"));

                        //습도 받기!
                        int tempDo1 = (Math.round((tempK1.getInt("humidity"))));
                        mainView.setText("습도 :" + tempDo1 +  "%");






                        JSONObject tempK2 = new JSONObject(jsonObject.getString("main"));

                        //최고온도 받기!
                        double tempDo2 = (Math.round((tempK2.getDouble("feels_like")-273.15)*100)/100.0);
                        mainView2.setText("체감온도 :" + tempDo2 +  "°C");



                        //JSONObject tempK3 = new JSONObject(jsonObject.getString("main"));

                        // 최소 온도 받기!
                        // double tempDo3 = (Math.round((tempK3.getDouble("temp_min")-273.15)*100)/100.0);
                        //mainView3.setText("최소 온도 :" + tempDo3 +  "°C");


                        //  JSONObject tempK4 = new JSONObject(jsonObject.getString("main"));

                        //최고 온도 받기!
                        // double tempDo4 = (Math.round((tempK4.getDouble("temp_max")-273.15)*100)/100.0);
                        // mainView4.setText("최고 온도 :" + tempDo4 +  "°C");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });
            }
        });
    }
}
