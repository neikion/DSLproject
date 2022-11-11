package com.example.dsl.bus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsl.R;
import com.example.dsl.schedule.TSListAdaptor;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class BusAlarmAdaptor extends RecyclerView.Adapter<BusAlarmAdaptor.BusListViewHolder>{
    ArrayList<BusAlarmDataSet> dataSets=new ArrayList<>();
    public BusAlarmAdaptor(){
    }
    @NonNull
    @Override
    public BusListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusAlarmAdaptor.BusListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_alarm,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BusListViewHolder holder, int position) {

    }
    @Override
    public void onViewAttachedToWindow(@NonNull BusListViewHolder holder) {
        holder.enable();
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BusListViewHolder holder) {
        holder.disable();
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return dataSets.size();
    }

    public class BusListViewHolder extends RecyclerView.ViewHolder{
        TextView name,time,bus;
        View item;
        public BusListViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView;
            name=item.findViewById(R.id.alarmlist_title);
            time=item.findViewById(R.id.alarmlist_time);
            bus=item.findViewById(R.id.alarmlist_bus);
        }
        public void enable(){
            name.setText(dataSets.get(getAdapterPosition()).AlarmTitle);
            time.setText(dataSets.get(getAdapterPosition()).getStringTime());
            bus.setText(dataSets.get(getAdapterPosition()).AlarmBus);
            item.setOnClickListener(v->{
                dataSets.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), 1);
                Snackbar.make(item,"삭제되었습니다.",Snackbar.LENGTH_SHORT).show();
            });

        }
        public void disable(){
            item.setOnClickListener(null);
        }
    }
}
