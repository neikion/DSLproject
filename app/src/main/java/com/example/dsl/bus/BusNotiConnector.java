package com.example.dsl.bus;

import android.app.Service;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class BusNotiConnector implements ServiceConnection {
    BusNotiService.BusNotiBinder binder;
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder= (BusNotiService.BusNotiBinder)service;
    }
    public BusNotiService getService(){
        return binder.getService();
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        binder=null;
    }
}
