package com.example.dsl.bus;

import java.io.Serializable;

public class BusDataSet implements Serializable {
    String BusName;
    String AlarmName;
    String arsId;
    boolean vibe;
    int time;

    @Override
    public String toString() {
        return "BusDataSet{" +
                "BusName='" + BusName + '\'' +
                ", AlarmName='" + AlarmName + '\'' +
                ", vibe=" + vibe +
                ", time=" + time +
                '}';
    }
}
