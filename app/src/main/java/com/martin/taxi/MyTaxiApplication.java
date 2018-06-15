package com.martin.taxi;

import android.app.Application;


public class MyTaxiApplication extends Application {
    private static MyTaxiApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance  = this;
    }
    public static MyTaxiApplication getInstance() {
        return  instance;
    }
}
