package com.example.dsl;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MenuBaseActivity extends AppCompatActivity implements View.OnClickListener {

    MenuFrame menuFrame;
    int menu_layout_id;
    protected MenuBaseActivity(MenuFrame frame,int menu_layout_id){
        menuFrame=frame;
        this.menu_layout_id=menu_layout_id;
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        menuFrame.onStart(findViewById(menu_layout_id));
    }

    @Override
    @CallSuper
    public void onClick(View v) {
        menuFrame.onClick(v);
    }
}
