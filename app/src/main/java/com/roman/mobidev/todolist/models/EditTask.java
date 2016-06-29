package com.roman.mobidev.todolist.models;

import android.database.Cursor;

import com.roman.mobidev.todolist.helpers.TaskFields;

import java.io.Serializable;

/**
 * Created by roman on 05.04.16.
 */
public class EditTask implements Serializable{



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }


    public EditTask(String name, String description, String expire, String status) {
        this.name = name;
        this.description = description;
        this.expire = expire;
        this.status = status;
    }



    public String name;
    public String description;
    public String expire;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
