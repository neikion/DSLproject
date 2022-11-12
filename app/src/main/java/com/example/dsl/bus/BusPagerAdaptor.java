package com.example.dsl.bus;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import java.util.ArrayList;

public class BusPagerAdaptor extends RecyclerView.Adapter<BusPagerAdaptor.BusPagerHolder> {
    ArrayList<String> dataSets=new ArrayList<>();
    public BusPagerAdaptor(){

    }
    public void addDataSets(String content){
        dataSets.add(content);
        notifyItemInserted(getItemCount()-1);
    }
    public void setDataSets(int pos,String content){
        dataSets.set(pos,content);
        notifyItemInserted(getItemCount()-1);
    }
    @NonNull
    @Override
    public BusPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusPagerHolder(new TextView(parent.getContext()));
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
            tv=(TextView) itemView;
            ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tv.setLayoutParams(params);
            tv.setPadding((int)DSLUtil.DPtoPX(5,tv.getContext()),(int)DSLUtil.DPtoPX(5,tv.getContext()),(int)DSLUtil.DPtoPX(5,tv.getContext()),(int)DSLUtil.DPtoPX(5,tv.getContext()));
        }
        public void setContent(){
            tv.setText(dataSets.get(getAdapterPosition()));
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BusPagerHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.setContent();
    }
}
