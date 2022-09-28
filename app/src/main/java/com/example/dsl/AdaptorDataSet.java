package com.example.dsl;

import java.io.Serializable;

public class AdaptorDataSet implements Serializable {
    //first 0 subject 1 professor
    //middle 0 day 1 start 2 end 3 place 4 sound 5 vibe
    //last
    public String subject="";
    public String professor="";
    public String place="";
    public int day=-1;
    public int start=-1;
    public int end=-1;
    public boolean soundSwitch=false;
    public boolean vibrateSwitch=false;
    public void setAlarmGroup(int id){
        if(id==0){
            soundSwitch=false;
            vibrateSwitch=false;
        }else if(id==1){
            soundSwitch=false;
            vibrateSwitch=true;
        }else if(id==2){
            soundSwitch=true;
            vibrateSwitch=false;
        }else{
            soundSwitch=true;
            vibrateSwitch=true;
        }
    }
    public int getAlarmGroup(){
        int result=0;
        if(vibrateSwitch){
            result++;
        }
        if(soundSwitch){
            result=2;
            if(vibrateSwitch){
                result++;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "AdaptorDataSet{" +
                "subject='" + subject + '\'' +
                ", professor='" + professor + '\'' +
                ", place='" + place + '\'' +
                ", day=" + day +
                ", start=" + start +
                ", end=" + end +
                ", soundSwitch=" + soundSwitch +
                ", vibrateSwitch=" + vibrateSwitch +
                '}';
    }
}
