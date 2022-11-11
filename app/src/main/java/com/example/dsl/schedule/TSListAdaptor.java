package com.example.dsl.schedule;

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

import com.example.dsl.R;
import com.example.dsl.notice.AdaptorDataSet;
import com.example.dsl.notice.CustomTimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TSListAdaptor extends RecyclerView.Adapter<TSListAdaptor.TSListViewHolder> {


    interface TSListener{
        void ChangeListener(ArrayList<AdaptorDataSet> list);
    }
    public class TSListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,TextWatcher, CompoundButton.OnCheckedChangeListener {
        private final View myView;
        private EditText subject_edit_text;
        private EditText pro;
        private TextView today;
        private View daybt;
        private TextView start;
        private TextView end;
        private EditText place;
        private TextView additem;
        private Button del;
        private Switch vibrateSwitch;
        private Switch soundSwitch;
        private Calendar startc=Calendar.getInstance();
        private Calendar endc=Calendar.getInstance();
        private AlertDialog.Builder daypicker;
        private final TimePickerDialog startpicker;
        private final TimePickerDialog endpicker;
        public TSListViewHolder(@NonNull View itemView) {
            super(itemView);
            initViewID(itemView);
            myView =itemView;
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
                    if(datalist.get(getAdapterPosition()).end!=-1){
                        intend=datalist.get(getAdapterPosition()).end;
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
                        datalist.get(getAdapterPosition()).end=(endc.get(Calendar.HOUR_OF_DAY)*100)+endc.get(Calendar.MINUTE);
                        end.setText(String.format(Locale.getDefault(),"%02d:%02d",endc.get(Calendar.HOUR_OF_DAY),endc.get(Calendar.MINUTE)));
                    }
                    start.setText(String.format(Locale.getDefault(),"%02d:%02d",hourOfDay,minute));
                    startpicker.updateTime(hourOfDay,minute);
                    datalist.get(getAdapterPosition()).start= (hourOfDay*100)+minute;
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
                    if(datalist.get(getAdapterPosition()).start!=-1){
                        intstart=datalist.get(getAdapterPosition()).start;
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
                        datalist.get(getAdapterPosition()).start=(startc.get(Calendar.HOUR_OF_DAY)*100)+startc.get(Calendar.MINUTE);
                    }
                    end.setText(String.format(Locale.getDefault(),"%02d:%02d",hourOfDay,minute));
                    endpicker.updateTime(endc.get(Calendar.HOUR_OF_DAY),endc.get(Calendar.MINUTE));
                    datalist.get(getAdapterPosition()).end=(hourOfDay*100)+minute;
                    listener.ChangeListener(datalist);
                }
            }, initendvalue, 0);
        }
        private void initViewID(@NonNull View itemView){
            int id = itemView.getId();
            if (id == R.id.ts_list_first_item_layout) {
                subject_edit_text = itemView.findViewById(R.id.subject);
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
            if(datalist.get(0)!=null){
                if(datalist.get(0).subject!=null){
                    datalist.get(position).subject=datalist.get(0).subject;
                }
                if(datalist.get(0).professor!=null){
                    datalist.get(position).professor=datalist.get(0).professor;
                }
            }
            datalist.get(position).day=initdayvalue;
            datalist.get(position).start=initstartvalue*100;
            datalist.get(position).end=initendvalue*100;
        }
        private void createDayPickerDialog(Context context){
            daypicker=new AlertDialog.Builder(context);
            daypicker.setItems(dayarray, (dialog, which) -> {
                today.setText(dayarray[which]);
                datalist.get(getAdapterPosition()).day=which;
                listener.ChangeListener(datalist);
            });
        }
        //timepicker 만들기
        private TimePickerDialog createTimePickerDialog(TimePickerDialog.OnTimeSetListener listener,int hour, int minute){
            TimePickerDialog result= new CustomTimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,listener,hour,minute,true,TIME_PICKER_INTERVAL);
            result.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            return result;
        }
        public void enableWatcher() {
            int id = myView.getId();
            if (id == R.id.ts_list_first_item_layout) {
                subject_edit_text.setText(datalist.get(0).subject);
                subject_edit_text.addTextChangedListener(this);

                pro.setText(datalist.get(0).professor);
                pro.addTextChangedListener(this);
            } else if (id == R.id.ts_list_item_layout) {
                today.setText(getmiddleValue(getAdapterPosition(),0));
                daybt.setOnClickListener(this);


                start.setText(getmiddleValue(getAdapterPosition(),1));
                start.setOnClickListener(this);


                end.setText(getmiddleValue(getAdapterPosition(),2));
                end.setOnClickListener(this);


                place.setText(datalist.get(getAdapterPosition()).place);
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
            switch (datapos) {
                case 0:
                    if(datalist.get(layoutpos).day==-1){
                        return dayarray[initdayvalue];
                    }else{
                        return dayarray[datalist.get(layoutpos).day];
                    }
                case 1:
                    if(datalist.get(layoutpos).start==-1){
                        return String.format(Locale.getDefault(),"%02d:00",initstartvalue);
                    }else{
                        int reuslt = datalist.get(layoutpos).start;
                        return String.format(Locale.getDefault(),"%02d:%02d",reuslt/100,reuslt%100);
                    }
                case 2:
                    if(datalist.get(layoutpos).end==-1){
                        return String.format(Locale.getDefault(),"%02d:00",initendvalue);
                    }else{
                        int reuslt = datalist.get(layoutpos).end;
                        return String.format(Locale.getDefault(),"%02d:%02d",reuslt/100,reuslt%100);
                    }
            }
            return null;
        }
        public void disableWatcher() {
            int id = myView.getId();
            if (id == R.id.ts_list_first_item_layout) {
                subject_edit_text.removeTextChangedListener(this);
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
            if(myView.getId()==R.id.ts_list_first_item_layout){
                if (s.hashCode()== subject_edit_text.getText().hashCode()) {
//                    datalist.get(0).subject=s.toString();
                    setListSubject(s.toString());
                }else{
//                    datalist.get(0).professor=s.toString();
                    setListProfessor(s.toString());
                }
            }
            else{
                datalist.get(getAdapterPosition()).place = s.toString();
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

    private void setListSubject(String subject){
        for(int i=0;i<datalist.size()-1;i++){
            datalist.get(i).subject=subject;
        }
    }
    private void setListProfessor(String professor){
        for(int i=0;i<datalist.size()-1;i++){
            datalist.get(i).professor=professor;
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
