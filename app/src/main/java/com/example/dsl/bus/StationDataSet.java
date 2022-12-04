package com.example.dsl.bus;

import java.io.Serializable;

public class StationDataSet implements Serializable {
    public String stationName;
    public String arsID;
    public String BusData;
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
    public StationDataSet(StationDataSet copy){
        stationName=copy.stationName;
        arsID=copy.arsID;
        BusData=copy.BusData;
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
