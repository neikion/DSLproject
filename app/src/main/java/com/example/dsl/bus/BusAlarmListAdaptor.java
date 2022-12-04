package com.example.dsl.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsl.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class BusAlarmListAdaptor extends RecyclerView.Adapter<BusAlarmListAdaptor.BusListViewHolder>{
    private ArrayList<BusDataSet> dataSets;
    setAction doAction;
    public interface setAction{
        void Action(int position,ArrayList<BusDataSet> list);
    }
    public BusAlarmListAdaptor(){
        dataSets=new ArrayList<>();
    }
    public void addBusDataSet(BusDataSet data,setAction action){
        dataSets.add(data);
        this.doAction=action;
        notifyItemInserted(getItemCount()-1);
    }
    public ArrayList<BusDataSet> getDataSets(){
        return new ArrayList<>(dataSets);
    }
    @NonNull
    @Override
    public BusListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusAlarmListAdaptor.BusListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_alarm,parent,false));
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
        TextView name,bus;
        View item;
        public BusListViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView;
            name=item.findViewById(R.id.alarmlist_title);
            bus=item.findViewById(R.id.alarmlist_bus);
        }
        public void enable(){
            name.setText(dataSets.get(getAdapterPosition()).AlarmName);
            bus.setText(dataSets.get(getAdapterPosition()).BusName);
            item.setOnLongClickListener(v->{
//                doAction.Action(dataSets.get(getAdapterPosition()));
                doAction.Action(getAdapterPosition(),getDataSets());
                dataSets.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), 1);
                Snackbar.make(item,"삭제되었습니다.",Snackbar.LENGTH_SHORT).show();
                return true;
            });

        }
        public void disable(){
            item.setOnClickListener(null);
        }
    }
}
