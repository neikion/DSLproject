package com.example.dsl.bus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsl.DSLUtil;
import com.example.dsl.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class BusPagerAdaptor extends RecyclerView.Adapter<BusPagerAdaptor.BusPagerHolder> {
    ArrayList<StationDataSet> dataSets=new ArrayList<>();
    public BusPagerAdaptor(){

    }
    public void addDataSets(String id, String content, TabLayout tabLayout, String tabname){
        tabLayout.addTab(tabLayout.newTab().setText(tabname));
        dataSets.add(new StationDataSet(id, tabname,content));
        notifyItemInserted(getItemCount()-1);
    }
    public void setDataSets(String id,String content){
        for(int i=0;i<dataSets.size();i++){
            if(dataSets.get(i).arsID.equals(id)){
                dataSets.get(i).BusData=content;
//                dataSets.set(i,new StationDataSet(id, content));
                notifyItemChanged(i);
            }
        }
    }
    public ArrayList<StationDataSet> getdataSets(){
        return new ArrayList<StationDataSet>(dataSets);
    }
    
    public void setDataSets(ArrayList<StationDataSet> Sets,TabLayout tabLayout){
        int deleteSize=dataSets.size();
        dataSets.clear();
        notifyItemRangeRemoved(0,deleteSize);
        tabLayout.removeAllTabs();
        dataSets=new ArrayList<>(Sets);
        for(int i=tabLayout.getTabCount();i<dataSets.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(dataSets.get(i).stationName));
        }
        if(getItemCount()>1){
            notifyItemRangeInserted(0,getItemCount());
        }else{
            notifyItemInserted(0);
        }

    }
    public ArrayList<String> getIdList(){
        ArrayList<String> result=new ArrayList<>();
        for(int i=0;i<dataSets.size();i++){
            result.add(dataSets.get(i).arsID);
        }
        return result;
    }
    public String getPosId(int listPos){
        return dataSets.get(listPos).arsID;
    }
    public int getIdCount(){
        return dataSets.size();
    }
    @NonNull
    @Override
    public BusPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_scroll_page,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BusPagerHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataSets.size();
    }

    class BusPagerHolder extends RecyclerView.ViewHolder{

        private TextView tv;
        public BusPagerHolder(@NonNull View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.bus_scroll_page_content);
            tv.setText("dd");
        }
        public void setContent(){
            tv.setText(dataSets.get(getAdapterPosition()).BusData);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BusPagerHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.setContent();
    }
}
