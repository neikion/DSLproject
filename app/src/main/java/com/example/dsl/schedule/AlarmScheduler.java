package com.example.dsl.schedule;

import java.util.ArrayList;

public class AlarmScheduler {

    private ArrayList<AlarmArray> schedule;
    public AlarmScheduler(int size){
        schedule=new ArrayList<>();
        for(int i=0;i<size;i++){
            schedule.add(new AlarmArray());
        }
    }
    public void add(int day, int start_time){
        schedule.get(day).add((day*10000)+start_time,start_time);
    }
    public AlarmArray get(int day){
        return schedule.get(day);
    }
    public int size(){
        return schedule.size();
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<schedule.size();i++){
            for(int i2=0;i2<schedule.get(i).size();i2++){
                sb.append("day "+i+" starttime "+schedule.get(i).getIndex(i2).start+"\n");
            }
        }
        return sb.toString();
    }

    class AlarmArray{
        private ArrayList<Alarmdata> list=new ArrayList<>();
        public void add(int request_code,int starttime){
            Alarmdata data=new Alarmdata(starttime);
            data.request_code =request_code;
            list.add(data);
            list.sort(Alarmdata::compareTo);
        }
        public void remove(int starttime){
            list.remove(getStartTimeIndex(starttime));
            list.sort(Alarmdata::compareTo);
        }
        public int size(){
            return list.size();
        }
        public void clear(){
            list.clear();
        }
        public Alarmdata get(int starttime){
            int index;
            int end=list.size(),start=0;
            while(start<=end){
                index=(end+start)/2;
                int picker=list.get(index).start;
                if(picker==starttime){
                    return list.get(index);
                }else if(picker<starttime){
                    start=index+1;
                }else{
                    end=index-1;
                }
            }
            return null;
        }
        public int getStartTimeIndex(int starttime){
            int index;
            int end=list.size(),start=0;
            while(start<=end){
                index=(end+start)/2;
                int picker=list.get(index).start;
                if(picker==starttime){
                    return index;
                }else if(picker<starttime){
                    start=index+1;
                }else{
                    end=index-1;
                }
            }
            return -1;
        }
        public Alarmdata getIndex(int index){
            return list.get(index);
        }
        public boolean Contains(int endtime){
            return get(endtime)!=null;
        }
    }
    class Alarmdata implements Comparable<Alarmdata> {
        public int start;
        public int request_code;

        public Alarmdata(int endtime){
            this.start =endtime;
        }
        @Override
        public int compareTo(Alarmdata o) {
            if(this.start <o.start){
                return -1;
            }else if(this.start > o.start){
                return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Alarmdata{" +
                    "start=" + start +
                    ", request_code=" + request_code +
                    '}';
        }
    }
}
