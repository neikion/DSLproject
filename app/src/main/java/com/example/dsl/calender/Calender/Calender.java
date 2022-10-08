package com.example.dsl.calender.Calender;

import java.io.Serializable;


public class Calender implements Serializable {
    private int userCode;//사용자의 id 학번일수도있음
    private int scheduleYear; //일정의 년도
    private int scheduleMonth; //일정의 월
    private int scheduleDay; //일정의 날짜
    private String title;
    private String scheduleContent; //일정의 내용
    private int scheduleID; //일정의 순번 자동으로 증가하게 만들예정 삭제도 이걸 기준으로 삭제할것임
    //요청시 해당 내용을 전부 보내고 수정 삭제시 scheduleID를 받아와서 수정할것예정


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calender(int userCode, int scheduleYear, int scheduleMonth, int scheduleDay, String title, String scheduleContent, int scheduleID) {
        this.title=title;
        this.userCode = userCode;
        this.scheduleYear = scheduleYear;
        this.scheduleMonth = scheduleMonth;
        this.scheduleDay = scheduleDay;
        this.scheduleContent = scheduleContent;
        this.scheduleID = scheduleID;
    }

    public Calender(int userCode, int scheduleYear, int scheduleMonth, int scheduleDay, String title, String scheduleContent) {
        this.userCode = userCode;
        this.scheduleYear = scheduleYear;
        this.scheduleMonth = scheduleMonth;
        this.scheduleDay = scheduleDay;
        this.title = title;
        this.scheduleContent = scheduleContent;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public int getScheduleYear() {
        return scheduleYear;
    }

    public void setScheduleYear(int scheduleYear) {
        this.scheduleYear = scheduleYear;
    }

    public int getScheduleMonth() {
        return scheduleMonth;
    }

    public void setScheduleMonth(int scheduleMonth) {
        this.scheduleMonth = scheduleMonth;
    }

    public int getScheduleDay() {
        return scheduleDay;
    }

    public void setScheduleDay(int scheduleDay) {
        this.scheduleDay = scheduleDay;
    }

    public String getScheduleContent() {
        return scheduleContent;
    }

    public void setScheduleContent(String scheduleContent) {
        this.scheduleContent = scheduleContent;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }
}
