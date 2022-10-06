package com.example.dsl.notice;



import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class CustomTimePickerDialog extends TimePickerDialog {

    private final int TIME_PICKER_INTERVAL;
    private TimePicker mTimePicker;
    private OnTimeSetListener listener;
    String[] str;
    public CustomTimePickerDialog(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView, int time_picker_interval) {
        super(context, themeResId, listener, hourOfDay, minute/time_picker_interval, is24HourView);
        TIME_PICKER_INTERVAL=time_picker_interval;
        this.listener=listener;
        str=new String[60/time_picker_interval];
        for (int i = 0; i < str.length; i ++) {
            str[i]=(String.format("%02d",(i*TIME_PICKER_INTERVAL)));
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (listener != null) {
                    listener.onTimeSet(mTimePicker, mTimePicker.getHour(), mTimePicker.getMinute() * TIME_PICKER_INTERVAL);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void updateTime(int hourOfDay, int minuteOfHour) {
        super.updateTime(hourOfDay, minuteOfHour/TIME_PICKER_INTERVAL);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTimePicker = findViewById(Resources.getSystem().getIdentifier("timePicker", "id", "android"));
        NumberPicker minutePicker = mTimePicker.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(str.length-1);
        minutePicker.setDisplayedValues(str);
    }

}
