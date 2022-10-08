package com.example.dsl.calender;
//미완성 유저 객체
public class User {
    private int userCode=9999;
    private String userName="dd";

    private User() {

    }
    private static User user = new User();

    public static User getUserInstance() {
        return user;
    }

    public int getUserCode() {
        return userCode;
    }

    public String getUserName() {
        return userName;
    }


}
