package com.example.dsl;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

public class DSLUtil {
    public static void print(String s){
        Log.i("DSL",s);
    }
    public static void print(Object s){
        Log.i("DSL",s.toString());
    }
    public static float DPtoPX(float dipValue, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
