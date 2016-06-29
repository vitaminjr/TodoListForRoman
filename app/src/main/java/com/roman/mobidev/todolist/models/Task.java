package com.roman.mobidev.todolist.models;

import android.database.Cursor;

import com.roman.mobidev.todolist.helpers.TaskFields;

import java.io.Serializable;

/**
 * Created by roman on 05.04.16.
 */
public class Task implements Serializable{

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Task createFromCursor(Cursor cursor){
        Task task = new Task();
        task.id = cursor.getInt(TaskFields._id.ordinal());
        task.name = cursor.getString(TaskFields.NAME.ordinal());
        task.description = cursor.getString(TaskFields.DESCRIPTION.ordinal());
        task.expire = cursor.getString(TaskFields.EXPIRE.ordinal());
        task.status = cursor.getString(TaskFields.STATUS.ordinal());
        return task;
    }
    private int id;
    private String name;
    private String description;
    private String expire;
    private String status;

}
