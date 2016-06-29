package com.roman.mobidev.todolist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.roman.mobidev.todolist.R;
import com.roman.mobidev.todolist.helpers.TaskFields;
import com.roman.mobidev.todolist.models.Task;

/**
 * Created by roman on 05.04.16.
 */
public class TasksCursorAdapter extends CursorAdapter {

    public TasksCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_tasks_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView taskName = (TextView) view.findViewById(R.id.i_task_name_textView);
        // Extract properties from cursor
        String name = cursor.getString(TaskFields.NAME.ordinal());
//        int priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority"));
        // Populate fields with extracted properties
        taskName.setText(name);

    }

    public Task getTaskFromPosition(int position){
        Cursor cursor = getCursor();
        Task task = null;
        if (cursor.moveToPosition(position)){
            task = Task.createFromCursor(cursor);
        }
        return task;
    }
}
