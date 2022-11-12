package com.example.dsl;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class MenuBaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected MenuFrame menuFrame;
    protected DrawerLayout menuLayout;
    int menu_layout_id;
    public MenuBaseActivity(){

    }

    public MenuBaseActivity(MenuFrame frame,int root_layout_id){
        menuFrame=frame;
        this.menu_layout_id=root_layout_id;
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();;
        menuLayout=findViewById(menu_layout_id);
        menuFrame.onStart(menuLayout);
    }

    @Override
    @CallSuper
    public void onClick(View v) {
        menuFrame.onClick(v);
    }
}
