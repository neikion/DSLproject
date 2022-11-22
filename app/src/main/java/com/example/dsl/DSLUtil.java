package com.example.dsl;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;

public class DSLUtil {
    public static void print(String s){
        Log.i("DSL",s);
    }
    public static void print(Object s){
        Log.i("DSL",s.toString());
    }
    public static void print(ArrayList<Object> s){
        for(int i=0;i<s.size();i++){
            Log.i("DSL",s.toString());
        }
    }
    public static float DPtoPX(float dipValue, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    public static class ScheduleColorList{
        public static int ColorSize=5;
        public static int getColor(Context context,int id){
            switch (id){
                case 0:
                    return context.getColor(R.color.pastel_red);
                case 1:
                    return context.getColor(R.color.pastel_blue);
                case 2:
                    return context.getColor(R.color.pastel_green);
                case 3:
                    return context.getColor(R.color.pastel_brown);
                case 4:
                    return context.getColor(R.color.pastel_orange);
                default:
                    return context.getColor(R.color.white);
            }
        }
    }
}
