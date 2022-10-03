package com.example.dsl;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NoticeAdapter extends ArrayAdapter<NoticeItem> {
    public NoticeAdapter(Context context, ArrayList<NoticeItem> NoticeItems) {
        super(context, 0, NoticeItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NoticeItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notice, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.txtName);
        // Populate the data into the template view using the data object
        tvName.setTextColor(Color.BLACK);
        tvName.setText(item.Name);
        // Return the completed view to render on screen
        return convertView;
    }
    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // Get the data item for this position
        NoticeItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notice, parent, false);
        }
        TextView tvName = convertView.findViewById(R.id.txtName);
        tvName.setTextColor(Color.BLACK);
        tvName.setText(item.Name);

        return tvName;
    }
}
