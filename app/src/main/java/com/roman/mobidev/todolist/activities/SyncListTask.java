package com.roman.mobidev.todolist.activities;

import android.os.AsyncTask;
import android.util.Log;

import com.roman.mobidev.todolist.db.TaskAsyncQueryHandler;
import com.roman.mobidev.todolist.models.Task;
import com.roman.mobidev.todolist.web.RetrofitHelper;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by vitaminjr on 31.05.16.
 */
public class SyncListTask extends AsyncTask<Void, Void, List<Task>> implements TaskAsyncQueryHandler.TaskAsyncQueryHandlerListener {

    static Response<List<Task>> responseListTask = null;
    static List<Task> taskList;
    private String tokenUser;
    private Call<List<Task>> callListTask;

    public SyncListTask(String token) {
        this.tokenUser = token;
    }

    @Override
    protected List<Task> doInBackground(Void... params) {

            Log.d("Task", tokenUser);
            callListTask = RetrofitHelper.getApiService().getTasksOfIndex(tokenUser);

            try {
                responseListTask = callListTask.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("massage", "massageGetTasks : " + responseListTask.code());
            taskList = responseListTask.body();
            Log.d("listSize", String.valueOf(taskList.size()));

        return taskList;
    }

    @Override
    public void onPostExecute(final Boolean success) {
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
