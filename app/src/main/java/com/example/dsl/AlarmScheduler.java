package com.example.dsl;

import java.util.ArrayList;

public class AlarmScheduler {

    private ArrayList<AlarmArray> schedule;
    public AlarmScheduler(int size){
        schedule=new ArrayList<>();
        for(int i=0;i<size;i++){
            schedule.add(new AlarmArray());
        }
    }
    public void add(int day, int endtime){
        schedule.get(day).add((day*10000)+endtime,endtime);
    }
    public AlarmArray get(int day){
        return schedule.get(day);
    }

    class AlarmArray{
        private ArrayList<Alarmdata> list;
        public void add(int requestcode,int endtime){
            Alarmdata data=new Alarmdata(endtime);
            data.requestcode=requestcode;
            list.add(data);
            list.sort(Alarmdata::compareTo);
        }
        public Alarmdata get(int endtime){
            int index;
            int end=list.size(),start=0;
            while(start<=end){
                index=(end+start)/2;
                int picker=list.get(index).endtime;
                if(picker==endtime){
                    return list.get(index);
                }else if(picker<endtime){
                    start=index+1;
                }else{
                    end=index-1;
                }
            }
            return null;
        }
        public boolean Contains(int endtime){
            return get(endtime)!=null;
        }
    }
    class Alarmdata implements Comparable<Alarmdata> {
        public int endtime;
        public int requestcode;

        public Alarmdata(int endtime){
            this.endtime=endtime;
        }
        @Override
        public int compareTo(Alarmdata o) {
            if(this.endtime<o.endtime){
                return -1;
            }else if(this.endtime> o.endtime){
                return 1;
            }
            return 0;
        }
    }
}
