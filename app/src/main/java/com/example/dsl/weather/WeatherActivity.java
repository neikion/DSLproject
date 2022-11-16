package com.example.dsl.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.dsl.DSLManager;
import com.example.dsl.DSLUtil;
import com.example.dsl.MenuBaseActivity;
import com.example.dsl.MenuCase1;
import com.example.dsl.MenuFrame;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherActivity extends MenuBaseActivity {

    TextView dateView;

    TextView cityView;
    TextView humidityView;
    TextView tempView;
    TextView mainView;
    TextView mainView2;
    Calendar weatherCalendar;
    TextView[] weaterFutureView;
    public WeatherActivity() {
        super(new MenuCase1(), R.id.weather_root);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherCalendar=Calendar.getInstance();
        dateView = findViewById(R.id.dateView);
        dateView.setOnClickListener((v)->{
            CurrentCall();
        });

        cityView = findViewById(R.id.weather_pos);
        humidityView = findViewById(R.id.humidityView);
        tempView = findViewById(R.id.tempView);
        mainView = findViewById(R.id.mainView);
        mainView2 = findViewById(R.id.mainView2);
        weaterFutureView=new TextView[]{
                findViewById(R.id.weather_future1),
                findViewById(R.id.weather_future2),
                findViewById(R.id.weather_future3),
                findViewById(R.id.weather_future4),
                findViewById(R.id.weather_future5),
                findViewById(R.id.weather_future6),
                findViewById(R.id.weather_future7),
        };
        findViewById(R.id.weather_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CurrentCall();
    }

    private void CurrentCall(){
        DSLManager.getInstance().sendRequest(this,null,"/weather/select",(result)->{
            runOnUiThread(()->{
                try {
                    JSONObject mainjson=result.getJSONObject(0);
                    Date date=new Date(mainjson.getLong("time")*1000);
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYY년 MM월 dd일 \n HH:mm");
                    dateView.setText(simpleDateFormat.format(date));
                    tempView.setText(mainjson.getInt("temp")+"º");
                    mainView.setText(mainjson.getInt("minTemp")+"º / "+mainjson.getInt("maxTemp")+"º   체감온도"+mainjson.getInt("feelsLike")+"º");
                    mainView2.setText(mainjson.getString("weatherState"));
                    humidityView.setText(mainjson.getString("humidity")+"%");

                    simpleDateFormat=new SimpleDateFormat("E요일");
                    for(int i=1;i<result.length();i++){
                        mainjson=result.getJSONObject(i);
                        date.setTime(mainjson.getLong("time")*1000);
                        weaterFutureView[i-1].setText(simpleDateFormat.format(date)
                                +"  "
                                +(mainjson.getInt("minTemp"))
                                +"º / "
                                +(mainjson.getInt("maxTemp"))
                                +"º  \n"
                                +mainjson.getString("weatherState")
                                +"\n"
                                +"강수확률 "
                                +mainjson.getString("pop")+"%"
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
