package com.example.dsl.bus;

import android.view.LayoutInflater;
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
import java.util.zip.Inflater;

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
        notifyItemChanged(pos);
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
