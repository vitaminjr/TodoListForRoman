package com.roman.mobidev.todolist.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.roman.mobidev.todolist.R;
import com.roman.mobidev.todolist.ToDoListApplication;
import com.roman.mobidev.todolist.adapters.TasksCursorAdapter;
import com.roman.mobidev.todolist.db.MyContentProvider;
import com.roman.mobidev.todolist.db.TaskAsyncQueryHandler;
import com.roman.mobidev.todolist.helpers.Loaders;
import com.roman.mobidev.todolist.helpers.TaskFields;
import com.roman.mobidev.todolist.models.Task;
import com.roman.mobidev.todolist.models.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TaskAsyncQueryHandler.TaskAsyncQueryHandlerListener {

    public static final int INTENT_ID = 100;

    private ListView tasksListView;
    private AlarmManager manager;
    private User user;
    Toolbar toolbar;
    ActionMode actionMode;
    TaskAsyncQueryHandler handler;
    Task taskDelete;
    private List<Task> taskList;
    private final int HANDLER_TOKEN = 300;
    Intent intent;
    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("Log", "onCreate");
        super.onCreate(savedInstanceState);
        user = ((ToDoListApplication) getApplication()).getUser();
        if ((user == null) || (!user.isLogged()) || LoginActivity.getToken() == null) {
            startActivity(intent = new Intent(this, LoginActivity.class));
        }

        setContentView(R.layout.activity_main);
        initGui();
        getSupportLoaderManager().initLoader
                (Loaders.tasks.ordinal(), null, this);

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
       handler = new TaskAsyncQueryHandler(getContentResolver(), this);
    }

    private void initGui() {
        //   toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        tasksListView = (ListView) findViewById(R.id.tasksListView);
        tasksListView.setAdapter(new TasksCursorAdapter(this, null, true));
        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startTaskDetailsActivity(parent, position);

            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            registerForContextMenu(tasksListView);
        }else {
            tasksListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            tasksListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    TasksCursorAdapter adapter = (TasksCursorAdapter) tasksListView.getAdapter();
                    taskDelete = adapter.getTaskFromPosition(position);
                    Log.d("taskDelete", String.valueOf(taskDelete.getId()));
                    Log.d("taskDelete", taskDelete.getDescription());
                    Log.d("taskDelete", taskDelete.getName());
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.menu_delete_task, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    Uri deleteUri = Uri.parse(MyContentProvider.TASKS_CONTENT_URI + "/" + taskDelete.getId());
                    getContentResolver().delete(deleteUri, null, null);
                    String userToken = LoginActivity.getToken();

                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (wifiInfo != null && wifiInfo.isConnected())
                    {
                        ProcessingOfTask processingOfTask = new ProcessingOfTask(userToken, taskDelete, 2);
                        processingOfTask.execute((Void) null);
                    }
                    wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    if (wifiInfo != null && wifiInfo.isConnected())
                    {
                        ProcessingOfTask processingOfTask = new ProcessingOfTask(userToken, taskDelete, 2);
                        processingOfTask.execute((Void) null);
                    }
                    wifiInfo = cm.getActiveNetworkInfo();
                    if (wifiInfo != null && wifiInfo.isConnected())
                    {
                    //    ProcessingOfTask processingOfTask = new ProcessingOfTask(userToken, taskDelete, 2);
                    //    processingOfTask.execute((Void) null);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"видалено без мережі",Toast.LENGTH_LONG).show();
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCreateNewActivity();
            }
        });
    }

    private void startCreateNewActivity() {
        Intent intent = TaskEditActivity.createNewTaskIntent(MainActivity.this);
        startActivityForResult(intent, TaskEditActivity.INTENT_ID);
    }


    private void startTaskDetailsActivity(AdapterView<?> parent, int position) {

        Task task = ((TasksCursorAdapter) parent.getAdapter()).getTaskFromPosition(position);
        Intent intent = TaskDetailsActivity.createTaskDetailsIntent(MainActivity.this, task);
        startActivityForResult(intent, TaskEditActivity.INTENT_ID);
    }

    @Override
    protected void onResume() {


     //   try {
     //       Thread.sleep(2000);
            if (LoginActivity.getToken() == null && flag == true) {

                startActivity(intent = new Intent(this, LoginActivity.class));
                Toast.makeText(getApplicationContext(),"Реєстрація невдала",Toast.LENGTH_SHORT).show();
            }
   //     } catch (InterruptedException e) {
     //       e.printStackTrace();
  //      }

        flag = true;

        TasksCursorAdapter adapter = (TasksCursorAdapter) tasksListView.getAdapter();
        adapter.notifyDataSetChanged();
        getSupportLoaderManager().getLoader(Loaders.tasks.ordinal()).forceLoad();
        Log.d("Log", "onResume");
/*        if (user != null) {
            Log.d("userToken", user.getJwt_token());
            String tokenUser = user.getJwt_token();
            Intent intent = new Intent(this, SynchronService.class);
            intent.putExtra("token", tokenUser);
            manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 40000, 10000, PendingIntent.getService(this, 0, intent, 0));
        }*/
        super.onResume();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (Loaders.values()[id]) {
            case tasks:
                return new CursorLoader(this,
                        MyContentProvider.TASKS_CONTENT_URI,
                        null, null, null, null);
            default:
                throw new IllegalArgumentException("Wrong loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ((TasksCursorAdapter) tasksListView.getAdapter()).
                swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((TasksCursorAdapter) tasksListView.getAdapter()).swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_synchrony_with_server, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_delete_task, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        TasksCursorAdapter adapter = (TasksCursorAdapter) tasksListView.getAdapter();
        taskDelete = adapter.getTaskFromPosition(position);
        Log.d("taskDelete", String.valueOf(taskDelete.getId()));
        Log.d("taskDelete", taskDelete.getDescription());
        Log.d("taskDelete", taskDelete.getName());
        Uri deleteUri = Uri.parse(MyContentProvider.TASKS_CONTENT_URI + "/" + taskDelete.getId());
        getContentResolver().delete(deleteUri, null, null);
        String userToken = LoginActivity.getToken();
        ProcessingOfTask processingOfTask = new ProcessingOfTask(userToken, taskDelete, 2);
        processingOfTask.execute((Void) null);
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            syncTask();
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            syncTask();
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
          //  syncTask();
        }
        else
            Toast.makeText(this,"Перевірте підключення",Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPostExecute(Boolean success) {
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

    public void syncTask(){
        String userToken = LoginActivity.getToken();
        SyncListTask syncListTask = new SyncListTask(userToken);
        syncListTask.execute((Void) null);

        try {
            taskList  = syncListTask.get();
            for (int i = 0; i < taskList.size() ; i++) {
                ContentValues values = new ContentValues();
                values.put(TaskFields._id.name(), taskList.get(i).getId());
                values.put(TaskFields.NAME.name(), taskList.get(i).getName());
                values.put(TaskFields.DESCRIPTION.name(), taskList.get(i).getDescription());
                values.put(TaskFields.EXPIRE.name(), taskList.get(i).getExpire());
                values.put(TaskFields.STATUS.name(), taskList.get(i).getStatus());

                handler.startInsert(HANDLER_TOKEN, null, MyContentProvider.TASKS_CONTENT_URI, values);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("sizeList",String.valueOf(taskList.size()));
    }
}
