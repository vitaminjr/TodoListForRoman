package com.roman.mobidev.todolist.activities;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.roman.mobidev.todolist.R;
import com.roman.mobidev.todolist.db.MyContentProvider;
import com.roman.mobidev.todolist.helpers.BundleKeys;
import com.roman.mobidev.todolist.helpers.Loaders;
import com.roman.mobidev.todolist.models.Task;

import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int INTENT_ID = 101;

    public static Intent createTaskDetailsIntent(Context context, Task task){
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(BundleKeys.TASK_OBJECT.name(), task);
        return  intent;
    }

    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView descriptionTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        initGui();
        Bundle bundle = getIntent().getExtras();
        Task task = (Task)bundle.getSerializable(BundleKeys.TASK_OBJECT.name());
        setData(task);
        getSupportLoaderManager().initLoader(Loaders.tasks.ordinal(), getIntent().getExtras(), this);
    }

    private void initGui() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditActivity();
            }
        });
    }

    private void setData(Task task) {
        collapsingToolbarLayout.setTitle(task.getName());
        descriptionTextView.setText(task.getDescription());

    }

    private void startEditActivity() {

        Task task = (Task)getIntent().getExtras().getSerializable(BundleKeys.TASK_OBJECT.name());
        Intent intent = TaskEditActivity.createEditTaskIntent(this, task);
        startActivityForResult(intent, TaskEditActivity.INTENT_ID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (Loaders.values()[id]){
            case tasks:
                Task task = (Task)bundle.getSerializable(BundleKeys.TASK_OBJECT.name());
               // Uri taskUri = ContentUris.withAppendedId(MyContentProvider.TASKS_CONTENT_URI, task.id);
                Uri taskUri = Uri.parse(MyContentProvider.TASKS_CONTENT_URI + "/" + task.getId());
                return new CursorLoader(this, taskUri, null, null, null, null);
            default:
                throw new IllegalArgumentException("Wrong loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        setData(Task.createFromCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
