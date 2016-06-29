package com.roman.mobidev.todolist.activities;

import android.app.Application;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.roman.mobidev.todolist.R;
import com.roman.mobidev.todolist.db.MyContentProvider;
import com.roman.mobidev.todolist.db.TaskAsyncQueryHandler;
import com.roman.mobidev.todolist.models.EditTask;
import com.roman.mobidev.todolist.models.Task;
import com.roman.mobidev.todolist.web.RetrofitHelper;

import java.io.IOException;
import java.security.AccessController;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by vitaminjr on 31.05.16.
 */
public class ProcessingOfTask extends AsyncTask<Void, Void, Task> implements TaskAsyncQueryHandler.TaskAsyncQueryHandlerListener {
    private Call<Task> callTask;
    Response<Task> responseTask = null;
    Task taskAdd;
    private String tokenUser;
    private int id;

    public ProcessingOfTask(String token, Task taskAdd, int id) {
        this.taskAdd = taskAdd;
        this.tokenUser = token;
        this.id = id;
    }
    public ProcessingOfTask(String token, int id) {
        this.tokenUser = token;
        this.id = id;
    }

    @Override
    protected Task doInBackground(Void... params) {

    switch (id) {
        case 0:
        Log.d("Task", String.valueOf(taskAdd.getId()));
        Log.d("Task", taskAdd.getName());
        Log.d("Task", taskAdd.getDescription());
        Log.d("Task", taskAdd.getExpire());
        Log.d("Task", taskAdd.getStatus());
        Log.d("Task", tokenUser);
            EditTask editTask = new EditTask(taskAdd.getName(),taskAdd.getDescription(),
                    taskAdd.getExpire(), taskAdd.getStatus());
            callTask = RetrofitHelper.getApiService().createTask(tokenUser,  editTask);

        try {
            responseTask = callTask.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("massage", "massageTaskCreate : " + responseTask.code());
            return responseTask.body();
        case 1:
            Log.d("Task", String.valueOf(taskAdd.getId()));
            Log.d("Task", taskAdd.getName());
            Log.d("Task", taskAdd.getDescription());
            Log.d("Task", taskAdd.getExpire());
            Log.d("Task", taskAdd.getStatus());
            Log.d("Task", tokenUser);

            callTask = RetrofitHelper.getApiService().updateTask(tokenUser, taskAdd, taskAdd.getId());
            try {
                responseTask = callTask.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("massage", "massageTaskUpdate : " + responseTask.code());
           break;
        case 2:
            Log.d("Task", String.valueOf(taskAdd.getId()));
            Log.d("Task", taskAdd.getName());
            Log.d("Task", taskAdd.getDescription());
            Log.d("Task", taskAdd.getExpire());
            Log.d("Task", taskAdd.getStatus());
            Log.d("Task", tokenUser);

            callTask = RetrofitHelper.getApiService().deleteTask(tokenUser, taskAdd.getId());

            try {
                responseTask = callTask.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("massage", "massageTaskDelete : " + responseTask.code());
            break;
    }
        return responseTask.body();
    }

    @Override
    public void onPostExecute(final Boolean success) {
     //   mAuthTask = null;
      //  showProgress(false);

        if (success) {
      //      finish();
        } else {
         //   mPasswordView.setError(getString(R.string.error_incorrect_password));
          //  mPasswordView.requestFocus();
        }
    }

    @Override
    public void onInsertComplete() {

    }

    @Override
    public void onDeleteComplete() {

    }

    @Override
    public void onUpdateComplete() {

    }
}
