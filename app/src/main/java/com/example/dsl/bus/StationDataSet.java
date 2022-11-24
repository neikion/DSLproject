package com.example.dsl.bus;

import java.io.Serializable;

public class StationDataSet implements Serializable {
    String stationName;
    String arsID;
    String BusData;
    public StationDataSet(){

    }
    public StationDataSet(String ID, String name){
        arsID=ID;
        stationName=name;
    }
    public StationDataSet(String ID, String name,String BusData){
        arsID=ID;
        stationName=name;
        this.BusData=BusData;
    }

    @Override
    public String toString() {
        return "StationDataSet{" +
                "stationName='" + stationName + '\'' +
                ", arsID='" + arsID + '\'' +
                ", BusData='" + BusData + '\'' +
                '}';
    }
}
