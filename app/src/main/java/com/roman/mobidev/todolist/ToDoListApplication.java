package com.roman.mobidev.todolist;

import android.app.Application;

import com.roman.mobidev.todolist.models.User;

/**
 * Created by roman on 25.04.16.
 */
public class ToDoListApplication extends Application {

    private User user;

    synchronized public void setUser(User user) {
        this.user = user;
    }

    synchronized public User getUser() {
        return user;
    }
}
