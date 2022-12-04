package com.example.dsl.bus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsl.R;

import java.util.ArrayList;

public class BusStationSettingAdaptor extends RecyclerView.Adapter<BusStationSettingAdaptor.BusStationHolder> {
    private ArrayList<StationDataSet> dataSets=new ArrayList<>();
    action listner;
    interface action{
        void action(int position, ArrayList<StationDataSet> list);
    }
    public BusStationSettingAdaptor(){
        dataSets=new ArrayList<>();
    }
    public BusStationSettingAdaptor(ArrayList<StationDataSet> list,action listner){
        if(list!=null){
            dataSets.clear();
            this.listner=listner;
            for(int i=0;i<list.size();i++){
                dataSets.add(list.get(i));
            }
            if(list.size()>0){
                notifyItemRangeInserted(0,getItemCount()-1);
            }
        }
    }
    public ArrayList<StationDataSet> getDataSets(){
        return new ArrayList<>(dataSets);
    }
    public void add(StationDataSet data,action listner){
        dataSets.add(data);
        this.listner=listner;
        notifyItemInserted(getItemCount()-1);
    }
    @NonNull
    @Override
    public BusStationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusStationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_choose_item,parent,false),listner);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStationHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataSets.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BusStationHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.enable();
    }

    class BusStationHolder extends RecyclerView.ViewHolder{
        TextView tv;
        action mylistenr;
        public BusStationHolder(@NonNull View itemView,action listenr) {
            super(itemView);
            tv=itemView.findViewById(R.id.choose_item_name);
            this.mylistenr =listenr;
        }
        public void enable(){
            tv.setText(dataSets.get(getAdapterPosition()).stationName);
            tv.setOnClickListener(v->{
                if(mylistenr !=null){
                    mylistenr.action(getAdapterPosition(), dataSets);
                }
            });
        }
    }
}
