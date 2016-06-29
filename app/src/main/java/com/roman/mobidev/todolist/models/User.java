package com.roman.mobidev.todolist.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roman on 25.04.16.
 */
public class User {

    private String email;
    private String password;

    public User() {
    }


    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.nickname = email;
    }


    public User(String password) {
        this.password = password;
    }

    public static User createFromEmailAndPassword(String email, String password){
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = email;
        return user;
    }

    public static User createEmailAndPassword(String email, String password){
        User user = new User();
        user.email = email;
        user.password = password;
        return user;
    }


    private int id;
    private String nickname;
    private String jwt_token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

   public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJwt_token() {
        return jwt_token;
    }

    public void setJwt_token(String jwt_token) {
        this.jwt_token = jwt_token;
    }

    public boolean isLogged(){
        return !TextUtils.isEmpty(jwt_token);
    }


}
