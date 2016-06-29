package com.roman.mobidev.todolist.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.regex.Pattern;
import com.roman.mobidev.todolist.R;
import com.roman.mobidev.todolist.db.MyContentProvider;
import com.roman.mobidev.todolist.db.TaskAsyncQueryHandler;
import com.roman.mobidev.todolist.helpers.BundleKeys;
import com.roman.mobidev.todolist.helpers.TaskFields;
import com.roman.mobidev.todolist.models.EditTask;
import com.roman.mobidev.todolist.models.Task;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TaskEditActivity extends AppCompatActivity implements TaskAsyncQueryHandler.TaskAsyncQueryHandlerListener {

    public static final int INTENT_ID = 101;
    private final int HANDLER_TOKEN = 300;
    private final int DIALOG_DATE = 1;
    int myYear = 2011;
    int myMonth = 02;
    int myDay = 03;
    int DIALOG_TIME = 2;
    int myHour = 14;
    int myMinute = 35;
    private EditText nameEditText;
    private TextView descriptionEditText;
    private TaskAsyncQueryHandler handler;
    private Button btndatepicker;
    private String date;
    private String time;
    private CheckBox checkStatus;
    private Task task;
    private static String  userToken;
    Task updateTask;
    private Button btnTimePicker;
    ProcessingOfTask processingOfTask = null;
    private boolean clickDate = false;
    private boolean clickTime = false;
    private enum Actions {
        create,
        edit
    }
    public static Intent createNewTaskIntent(Context context){
        Intent intent = new Intent(context, TaskEditActivity.class);
        intent.setAction(Actions.create.name());
        return  intent;
    }
    public static Intent createEditTaskIntent(Context context, Task task){
        Intent intent = new Intent(context, TaskEditActivity.class);
        intent.setAction(Actions.edit.name());
        intent.putExtra(BundleKeys.TASK_OBJECT.name(), task);
        return  intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
            init();
        userToken = LoginActivity.getToken();
        handler = new TaskAsyncQueryHandler(getContentResolver(), this);


        if (isEditMode()) {
            Task editableTask = (Task)getIntent().getExtras().getSerializable(BundleKeys.TASK_OBJECT.name());
            nameEditText.setText(editableTask.getName());
            descriptionEditText.setText(editableTask.getDescription());


            if(editableTask.getStatus().equals("resolved"))
            {
                checkStatus.setChecked(true);
            }
            else
                checkStatus.setChecked(false);
            String getDate =  editableTask.getExpire();



            String[] masDate =  getDate.replace('T',' ').split(" ");
            btndatepicker.setText(masDate[0]);
            String[] date = masDate[0].split("-");
            myYear = Integer.parseInt(date[0]);
            myMonth = Integer.parseInt(date[1]);
            myDay = Integer.parseInt(date[2]);
            String[] mastime = masDate[1].split(":");
            myHour = Integer.parseInt(mastime[0]);
            myMinute = Integer.parseInt(mastime[1]);
            String[] ttime = masDate[1].replace('.',' ').split(" ");
            btnTimePicker.setText(ttime[0]);

            clickTime = false;
            clickDate = false;


        }

        btndatepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME);
            }
        });
    }


    protected Dialog onCreateDialog(int id)
    {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        else if(id == DIALOG_TIME)
        {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBackTime, myHour, myMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }



    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;

            date = (myYear + "-" + myMonth + "-" + myDay);
            btndatepicker.setText(date);
            clickDate = true;
        }
    };



    TimePickerDialog.OnTimeSetListener myCallBackTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            time = (myHour + ":" + myMinute);
            btnTimePicker.setText(time);
            clickTime = true;
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.d("createTask", "createTaskSelected");
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if (isEditMode()){

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiInfo != null && wifiInfo.isConnected())
                {
                    updateTask();
                }
                wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiInfo != null && wifiInfo.isConnected())
                {
                    updateTask();
                }
                wifiInfo = cm.getActiveNetworkInfo();
                if (wifiInfo != null && wifiInfo.isConnected())
                {
//                    updateTask();
                }
                else
                    Toast.makeText(this,"Перевірте підключення",Toast.LENGTH_LONG).show();
               // updateTask();

            }else{

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiInfo != null && wifiInfo.isConnected())
                {
                    createNewTask();
                }
                wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiInfo != null && wifiInfo.isConnected())
                {
                    createNewTask();
                }
                wifiInfo = cm.getActiveNetworkInfo();
                if (wifiInfo != null && wifiInfo.isConnected())
                {
               //     createNewTask();
                }
                else
                    Toast.makeText(this,"Перевірте підключення",Toast.LENGTH_LONG).show();
            }
            return true;
        }

            return super.onOptionsItemSelected(item);

    }

    private void createNewTask() {

        if (clickDate == false || clickTime == false)
            Toast.makeText(this,"Введіть дату та час", Toast.LENGTH_SHORT).show();
        else {
            if(checkStatus.isChecked()==true)
                task.setStatus("resolved");
            else
                task.setStatus("active");
            task.setName(nameEditText.getText().toString());
            task.setDescription(descriptionEditText.getText().toString());
            task.setExpire(date + "T" + time + ":00");
            processingOfTask = new ProcessingOfTask(userToken, task, 0);
            processingOfTask.execute((Void) null);
            try {
                task = processingOfTask.get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.getMessage();
            }
            if (task != null) {
                ContentValues values = new ContentValues();
                values.put(TaskFields._id.name(), task.getId());
                values.put(TaskFields.NAME.name(), task.getName());
                values.put(TaskFields.DESCRIPTION.name(), task.getDescription());
                values.put(TaskFields.STATUS.name(), task.getStatus());
                values.put(TaskFields.EXPIRE.name(), task.getExpire());
                handler.startInsert(HANDLER_TOKEN, null, MyContentProvider.TASKS_CONTENT_URI, values);
                Toast.makeText(this, "Завдання добавлено", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateTask() {

        updateTask = (Task)getIntent().getExtras().getSerializable(BundleKeys.TASK_OBJECT.name());

        if (clickDate == false || clickTime == false)
            Toast.makeText(this,"Введіть дату та час", Toast.LENGTH_SHORT).show();
        else {
            if(checkStatus.isChecked()==true)
            {
                updateTask.setStatus("resolved");
            }
            else {
                updateTask.setStatus("active");
            }
            updateTask.setName(nameEditText.getText().toString());
            updateTask.setDescription(descriptionEditText.getText().toString());
            updateTask.setExpire(date + "T" + time + ":00");


            processingOfTask = new ProcessingOfTask(userToken, updateTask, 1);
            Log.d("LogIDTASk", String.valueOf(updateTask.getId()));
            processingOfTask.execute((Void) null);

            try {
                updateTask = processingOfTask.get();
                Log.d("DateUpdate", updateTask.getExpire());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (updateTask != null) {
                Log.d("DateUpdate", updateTask.getExpire());
                ContentValues values = new ContentValues();
                values.put(TaskFields.NAME.name(), updateTask.getName());

                values.put(TaskFields.DESCRIPTION.name(), updateTask.getDescription());
                values.put(TaskFields.EXPIRE.name(), updateTask.getExpire());
                values.put(TaskFields.STATUS.name(), updateTask.getStatus());
                Uri updateUri = ContentUris.withAppendedId(MyContentProvider.TASKS_CONTENT_URI, updateTask.getId());
                handler.startUpdate(HANDLER_TOKEN, null, updateUri, values, null, null);
            }
        }
    }

    private boolean isEditMode(){
        return getIntent().getAction().equalsIgnoreCase(Actions.edit.name());
    }

    @Override
    public void onPostExecute(Boolean success) {

    }

    @Override
    public void onInsertComplete() {
        closeActivity();
    }

    @Override
    public void onDeleteComplete() {

    }

    @Override
    public void onUpdateComplete() {
        closeActivity();
    }

    private void closeActivity(){
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void init(){
        task = new Task();
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        descriptionEditText = (EditText)findViewById(R.id.descriptionEditText);
        btndatepicker = (Button) findViewById(R.id.button_datePicker);
        btnTimePicker = (Button) findViewById(R.id.button_timePicker);
        checkStatus = (CheckBox) findViewById(R.id.status_check);
    }
}
