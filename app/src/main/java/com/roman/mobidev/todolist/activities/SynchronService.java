package com.roman.mobidev.todolist.activities;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.roman.mobidev.todolist.models.Task;
import com.roman.mobidev.todolist.models.User;
import com.roman.mobidev.todolist.web.RetrofitHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class SynchronService extends IntentService {

    Call<Task> callTask;


    public SynchronService() {
        super("service");
    }


    @Override
    public void onCreate() {
        Log.d("Log","Log Service create");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.d("Log", "Start Service");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    //   callTask = RetrofitHelper.getApiService().createTask(Task.createFromCursor());

someTask();
  //      String s = intent.getStringExtra("token");
//        Log.d("Intent", s);

/*
new Thread(new Runnable() {
    @Override
    public void run() {
        for (int i = 0; i < 100 ; i++) {
            Log.d("count", String.valueOf(i));
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", Locale.getDefault());
                final String str = simpleDateFormat.format(calendar.getTime());
                Log.d("Timer",str);

            }
        };
        timer.schedule(timerTask,100, 500);


        Log.d("Log", "!!!Start Service!!!");
    }
});
*/

        return super.onStartCommand(intent, flags, startId);
    }


    void someTask() {
        new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i<=5; i++) {
                    Log.d("LogC", "i = " + i);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }).start();
    }
}
