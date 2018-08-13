package com.github.bluetoothchat;

/**
 * Created by pallav.choudhary on 11-07-2017.
 */

public class User {

    private int user_id;
    private String user_name;
    private String user_status;

    public User(){}

    public User(String user_name, String user_status){
        this.user_name = user_name;
        this.user_status = user_status;
    }

    public void setUserId(int id){
        this.user_id = id;
    }

    public int getUserId(){
        return this.user_id;
    }

    public void setUserName(String name){
        this.user_name = name;
    }

    public String getUserName(){
        return this.user_name;
    }

    public void setUserStatus(String status){
        this.user_status = status;
    }

    public String getUserStatus(){
        return this.user_status;
    }
}
