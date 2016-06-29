package com.roman.mobidev.todolist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    ViewHolder viewHolder;

    public TasksCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.item_tasks_list, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        viewHolder = (ViewHolder) view.getTag();

        if (viewHolder == null){
      //      LayoutInflater inflater = (LayoutInflater) context
      //              .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //        view = inflater.inflate(R.layout.item_tasks_list, null, true);
            viewHolder = new ViewHolder();
            Log.d("Log","use_method_new_view");
            viewHolder.textView = (TextView) view.findViewById(R.id.i_task_name_textView);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
            Log.d("Log","get");
        }
        Log.d("Log","iterator");
        String name = cursor.getString(TaskFields.NAME.ordinal());
        viewHolder.textView.setText(name);
    }

    public Task getTaskFromPosition(int position){
        Cursor cursor = getCursor();
        Task task = null;
        if (cursor.moveToPosition(position)){
            task = Task.createFromCursor(cursor);
        }
        return task;
    }

    static class ViewHolder {
        TextView textView;
    }
}
