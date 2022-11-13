package com.example.dsl.bus;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BusListAdaptor extends RecyclerView.Adapter<BusListAdaptor.BusListHolder> {
    ArrayList<String> dataSets=new ArrayList<>();
    @NonNull
    @Override
    public BusListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_choose_bus,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BusListHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataSets.size();
    }
    public void addDataset(JSONObject json) throws Exception {
        //경유 노선 버스 json처리
        dataSets.add(json.getString("busRouteAbrv"));
        notifyItemInserted(getItemCount()-1);
    }
    public class BusListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public BusListHolder(@NonNull View itemView) {
            super(itemView);
        }
        public void enable(){
            ((TextView)itemView.findViewById(R.id.choose_bus_name)).setText(dataSets.get(getAdapterPosition()));
            itemView.findViewById(R.id.choose_bus_name).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i=new Intent();
            i.putExtra("BusName",dataSets.get(getAdapterPosition()));
            ((ChooseBusActivity)v.getContext()).setResult(Activity.RESULT_OK,i);
            ((ChooseBusActivity)v.getContext()).finish();
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BusListHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.enable();
    }
}
