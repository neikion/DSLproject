package com.example.dsl;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TSListAdaptor extends RecyclerView.Adapter<TSListAdaptor.TSListViewHolder> {


    interface TSListener{
        void ChangeListener(ArrayList<AdaptorDataSet> list);
    }
    public class TSListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,TextWatcher, CompoundButton.OnCheckedChangeListener {
        private final View view;
        EditText sub;
        EditText pro;
        TextView today;
        View daybt;
        TextView start;
        TextView end;
        EditText place;
        TextView additem;
        Button del;
        Switch vibrateSwitch;
        Switch soundSwitch;
        Calendar startc=Calendar.getInstance();
        Calendar endc=Calendar.getInstance();
        private AlertDialog.Builder daypicker;
        private final TimePickerDialog startpicker;
        private final TimePickerDialog endpicker;
        public TSListViewHolder(@NonNull View itemView) {
            super(itemView);
            initViewID(itemView);
            view=itemView;
            createDayPickerDialog(itemView.getContext());
            startpicker=createTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if(hourOfDay==23&&minute==55){
                        minute=50;
                    }
                    startc.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    startc.set(Calendar.MINUTE,minute);
                    int intend;
                    if(datalist.get(getAdapterPosition()).datas[2]!=null){
                        intend=Integer.parseInt(datalist.get(getAdapterPosition()).datas[2]);
                    }else{
                        intend=initendvalue*100;
                    }
                    endc.set(Calendar.HOUR_OF_DAY,intend/100);
                    endc.set(Calendar.MINUTE,intend%100);
                    if(startc.compareTo(endc)>=0){
                        endc.set(Calendar.HOUR_OF_DAY,startc.get(Calendar.HOUR_OF_DAY));
                        if(endc.get(Calendar.HOUR_OF_DAY)==23){
                            endc.set(Calendar.MINUTE,55);
                        }else{
                            endc.set(Calendar.MINUTE,startc.get(Calendar.MINUTE));
                            endc.add(Calendar.HOUR_OF_DAY,1);
                        }
                        endpicker.updateTime(endc.get(Calendar.HOUR_OF_DAY),endc.get(Calendar.MINUTE));
                        datalist.get(getAdapterPosition()).datas[2]=String.format(Locale.getDefault(),"%02d%02d",endc.get(Calendar.HOUR_OF_DAY),endc.get(Calendar.MINUTE));
                        end.setText(String.format(Locale.getDefault(),"%02d:%02d",endc.get(Calendar.HOUR_OF_DAY),endc.get(Calendar.MINUTE)));
                    }
                    start.setText(String.format(Locale.getDefault(),"%02d:%02d",hourOfDay,minute));
                    startpicker.updateTime(hourOfDay,minute);
                    datalist.get(getAdapterPosition()).datas[1]=String.format(Locale.getDefault(),"%02d%02d",hourOfDay,minute);
                    listener.ChangeListener(datalist);
                }
            },initstartvalue,0);
            endpicker=createTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if(hourOfDay==0&&minute==0){
                        hourOfDay=23;
                        minute=55;
                        endpicker.updateTime(23,11);
                    }
                    endc.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    endc.set(Calendar.MINUTE,minute);
                    int intstart;
                    if(datalist.get(getAdapterPosition()).datas[1]!=null){
                        intstart=Integer.parseInt(datalist.get(getAdapterPosition()).datas[1]);
                    }else{
                        intstart=initstartvalue*100;
                    }
                    startc.set(Calendar.HOUR_OF_DAY,intstart/100);
                    startc.set(Calendar.MINUTE,intstart%100);
                    if(startc.compareTo(endc)>=0){

                        startc.set(Calendar.HOUR_OF_DAY,endc.get(Calendar.HOUR_OF_DAY));
                        if(startc.get(Calendar.HOUR)==0){
                            startc.set(Calendar.MINUTE,0);
                        }else{
                            startc.set(Calendar.MINUTE,endc.get(Calendar.MINUTE));
                            startc.add(Calendar.HOUR_OF_DAY,-1);
                        }

                        startpicker.updateTime(startc.get(Calendar.HOUR_OF_DAY),startc.get(Calendar.MINUTE));
                        start.setText(String.format(Locale.getDefault(),"%02d:%02d",startc.get(Calendar.HOUR_OF_DAY),startc.get(Calendar.MINUTE)));
                        datalist.get(getAdapterPosition()).datas[1]=String.format(Locale.getDefault(),"%02d%02d",startc.get(Calendar.HOUR_OF_DAY),startc.get(Calendar.MINUTE));
                    }
                    end.setText(String.format(Locale.getDefault(),"%02d:%02d",hourOfDay,minute));
                    endpicker.updateTime(endc.get(Calendar.HOUR_OF_DAY),endc.get(Calendar.MINUTE));
                    datalist.get(getAdapterPosition()).datas[2]=String.format(Locale.getDefault(),"%02d%02d",hourOfDay,minute);
                    listener.ChangeListener(datalist);
                }
            }, initendvalue, 0);
        }
        private void initViewID(@NonNull View itemView){
            int id = itemView.getId();
            if (id == R.id.ts_list_first_item_layout) {
                sub = itemView.findViewById(R.id.subject);
                pro = itemView.findViewById(R.id.professor);
            } else if (id == R.id.ts_list_item_layout) {
                daybt=itemView.findViewById(R.id.daybutton);
                today = itemView.findViewById(R.id.today);
                start = itemView.findViewById(R.id.starttime);
                end = itemView.findViewById(R.id.endtime);
                place = itemView.findViewById(R.id.place);
                del = itemView.findViewById(R.id.list_delete);
                vibrateSwitch=itemView.findViewById(R.id.vibrateSwitch);
                soundSwitch=itemView.findViewById(R.id.soundSwitch);
            } else if (id == R.id.ts_list_last_item_layout) {
                additem = itemView.findViewById(R.id.list_add_item);
            }
        }
        private void initMiddleItem(int position){
            datalist.get(position).datas[0]=String.valueOf(initdayvalue);
            datalist.get(position).datas[1]=String.format(Locale.getDefault(),"%02d00",initstartvalue);
            datalist.get(position).datas[2]=String.format(Locale.getDefault(),"%02d00",initendvalue);
        }
        public void createDayPickerDialog(Context context){
            daypicker=new AlertDialog.Builder(context);
            daypicker.setItems(dayarray, (dialog, which) -> {
                today.setText(dayarray[which]);
                datalist.get(getAdapterPosition()).datas[0]=Integer.toString(which);
                listener.ChangeListener(datalist);
            });
        }
        //timepicker 만들기
        public TimePickerDialog createTimePickerDialog(TimePickerDialog.OnTimeSetListener listener,int hour, int minute){
            TimePickerDialog result= new CustomTimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,listener,hour,minute,true,TIME_PICKER_INTERVAL);
            result.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            return result;
        }
        void enableWatcher() {
            int id = view.getId();
            if (id == R.id.ts_list_first_item_layout) {
                sub.setText(datalist.get(0).datas[0]);
                sub.addTextChangedListener(this);

                pro.setText(datalist.get(0).datas[1]);
                pro.addTextChangedListener(this);
            } else if (id == R.id.ts_list_item_layout) {
                today.setText(getmiddleValue(getAdapterPosition(),0));
                daybt.setOnClickListener(this);


                start.setText(getmiddleValue(getAdapterPosition(),1));
                start.setOnClickListener(this);


                end.setText(getmiddleValue(getAdapterPosition(),2));
                end.setOnClickListener(this);


                place.setText(datalist.get(getAdapterPosition()).datas[3]);
                place.addTextChangedListener(this);

                soundSwitch.setChecked(datalist.get(getAdapterPosition()).soundSwitch);
                soundSwitch.setOnCheckedChangeListener(this);

                vibrateSwitch.setChecked(datalist.get(getAdapterPosition()).vibrateSwitch);
                vibrateSwitch.setOnCheckedChangeListener(this);

                del.setOnClickListener(this);
            } else if (id == R.id.ts_list_last_item_layout) {
                additem.setOnClickListener(this);
            }
        }
        private String getmiddleValue(int layoutpos,int datapos){
            if(datalist.get(layoutpos).datas[datapos]==null) {
                switch (datapos) {
                    case 0:
                        return dayarray[initdayvalue];
                    case 1:
                        return String.format(Locale.getDefault(),"%02d:00",initstartvalue);
                    case 2:
                        return String.format(Locale.getDefault(),"%02d:00",initendvalue);
                }
            }
            if(datapos==0){
                return dayarray[Integer.parseInt(datalist.get(layoutpos).datas[datapos])];
            }
            String reuslt = datalist.get(layoutpos).datas[datapos];
            return reuslt.substring(0,2)+":"+reuslt.substring(2);
        }
        void disableWatcher() {
            int id = view.getId();
            if (id == R.id.ts_list_first_item_layout) {
                sub.removeTextChangedListener(this);
                pro.removeTextChangedListener(this);
            } else if (id == R.id.ts_list_item_layout) {
                daybt.setOnClickListener(null);
                start.setOnClickListener(null);
                end.setOnClickListener(null);
                place.removeTextChangedListener(this);
                soundSwitch.setOnCheckedChangeListener(null);
                vibrateSwitch.setOnCheckedChangeListener(null);
            } else if (id == R.id.ts_list_last_item_layout) {
                additem.setOnClickListener(null);
            }
        }
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.list_delete) {
                int position = getAdapterPosition();
                datalist.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, 1);
                listener.ChangeListener(datalist);
            } else if (id == R.id.daybutton) {
                //날짜할당
                daypicker.show();
            } else if (id == R.id.starttime) {
                startpicker.show();
            } else if (id == R.id.endtime) {
                endpicker.show();
            } else if (id == R.id.list_add_item) {
                setArrayData();
                notifyItemInserted(getItemCount()-1);
                initMiddleItem(getItemCount()-2);
                listener.ChangeListener(datalist);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(view.getId()==R.id.ts_list_first_item_layout){
                if (s.hashCode()==sub.getText().hashCode()) {
                    datalist.get(getAdapterPosition()).datas[0] = s.toString();
                }else{
                    datalist.get(getAdapterPosition()).datas[1] = s.toString();
                }
            }
            else{
                datalist.get(getAdapterPosition()).datas[3] = s.toString();
            }
            listener.ChangeListener(datalist);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.isPressed()){
                if(buttonView.equals(soundSwitch)){
                    datalist.get(getAdapterPosition()).soundSwitch=isChecked;
                }
                if(buttonView.equals(vibrateSwitch)){
                    datalist.get(getAdapterPosition()).vibrateSwitch=isChecked;
                }
            }

        }
    }


    //todo static class에서 일반 class로 바꿔주기
    public static class AdaptorDataSet implements Serializable {
        public String[] datas;
        //first 0 subject 1 professor
        //middle 0 day 1 start 2 end 3 place 4 sound 5 vibe
        //last
        public AdaptorDataSet(){
            datas =new String[4];
        }
        public boolean soundSwitch=false;
        public boolean vibrateSwitch=false;
    }


    private ArrayList<AdaptorDataSet> datalist;
    private final Context context;
    public final String[] dayarray=new String[]{"월요일","화요일","수요일","목요일","금요일","토요일","일요일"};
    private final int initdayvalue;
    private final int initstartvalue;
    private final int initendvalue;
    private final int TIME_PICKER_INTERVAL=5;
    private TSListener listener;
    public TSListAdaptor(Context context,int initdayvalue, int initstartvalue, int initendvalue,TSListener listener){
        datalist=new ArrayList<>();
        this.context =context;
        this.initdayvalue= initdayvalue;
        this.initstartvalue=initstartvalue;
        this.initendvalue=initendvalue;
        this.listener=listener;
        //기본 과목, 교수 아이템과 추가 아이템 생성
        setArrayData();
        setArrayData();
    }


    @NonNull
    @Override
    public TSListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0){
            return new TSListViewHolder(LayoutInflater.from(context).inflate(R.layout.ts_list_first_item,parent,false));
        }else if(viewType==-1){
            return new TSListViewHolder(LayoutInflater.from(context).inflate(R.layout.ts_list_add_item,parent,false));
        }else{
            return new TSListViewHolder(LayoutInflater.from(context).inflate(R.layout.ts_list_item,parent,false));
        }


    }

    @Override
    public void onBindViewHolder(@NonNull TSListViewHolder holder, int position) {
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            //과목, 교수 아이템 first
            return 0;
        }else if(position==getItemCount()-1){
            //마지막
            return -1;
        }
        else{
            //처음
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public void setArrayData(){
        datalist.add(new AdaptorDataSet());
    }
    public void setArrayData(int position, AdaptorDataSet dataset){
        datalist.add(position,dataset);
    }
    public void clearArrayData(){
        datalist.clear();
    }

    public ArrayList<AdaptorDataSet> getArrayData(){return datalist;}

    @Override
    public void onViewAttachedToWindow(@NonNull TSListViewHolder holder) {
        holder.enableWatcher();
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TSListViewHolder holder) {
        holder.disableWatcher();
        super.onViewDetachedFromWindow(holder);
    }
}
