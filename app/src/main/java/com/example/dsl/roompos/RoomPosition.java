package com.example.dsl.roompos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.dsl.R;

public class RoomPosition extends AppCompatActivity {
    Button[] btnMap;
    Button[] btnNav;
    Button beforeBtn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        btnMap = new Button[12];
        btnNav = new Button[12];
        bindingButton();
        for (int i = 0; i < btnNav.length; i++) {
            Button btn = btnMap[i];
            btnNav[i].setOnClickListener(view -> {
                if (beforeBtn != null) {
                    beforeBtn.setBackgroundResource(R.drawable.room_circle);
                }
                btn.setBackgroundResource(R.drawable.room_circlecolor);
                beforeBtn = btn;
            });
        }

    }

    private void bindingButton() {
        btnMap[0] = findViewById(R.id.btn1);
        btnMap[1] = findViewById(R.id.btn2);
        btnMap[2] = findViewById(R.id.btn3);
        btnMap[3] = findViewById(R.id.btn4);
        btnMap[4] = findViewById(R.id.btn5);
        btnMap[5] = findViewById(R.id.btn6);
        btnMap[6] = findViewById(R.id.btn7);
        btnMap[7] = findViewById(R.id.btn8);
        btnMap[8] = findViewById(R.id.btn9);
        btnMap[9] = findViewById(R.id.btn10);
        btnMap[10] = findViewById(R.id.btn11);
        btnMap[11] = findViewById(R.id.btn12);

        btnNav[0] = findViewById(R.id.btn_nav_1);
        btnNav[1] = findViewById(R.id.btn_nav_2);
        btnNav[2] = findViewById(R.id.btn_nav_3);
        btnNav[3] = findViewById(R.id.btn_nav_4);
        btnNav[4] = findViewById(R.id.btn_nav_5);
        btnNav[5] = findViewById(R.id.btn_nav_6);
        btnNav[6] = findViewById(R.id.btn_nav_7);
        btnNav[7] = findViewById(R.id.btn_nav_8);
        btnNav[8] = findViewById(R.id.btn_nav_9);
        btnNav[9] = findViewById(R.id.btn_nav_10);
        btnNav[10] = findViewById(R.id.btn_nav_11);
        btnNav[11] = findViewById(R.id.btn_nav_12);
    }
}