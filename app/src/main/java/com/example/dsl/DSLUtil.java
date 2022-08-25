package com.example.dsl;

import android.util.Log;

public class DSLUtil {
    public static void print(String s){
        Log.i("DSL",s);
    }
    public static void print(Object s){
        Log.i("DSL",s.toString());
    }
}
