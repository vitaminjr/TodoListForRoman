package com.roman.mobidev.todolist.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by roman on 05.04.16.
 */
public class TaskAsyncQueryHandler extends AsyncQueryHandler {

    public interface TaskAsyncQueryHandlerListener{
        void onPostExecute(Boolean success);

        void onInsertComplete();
        void onDeleteComplete();
        void onUpdateComplete();
    }

    private TaskAsyncQueryHandlerListener listener;

    public TaskAsyncQueryHandler(ContentResolver cr, TaskAsyncQueryHandlerListener listener) {
        super(cr);
        this.listener = listener;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        if(listener != null){
            listener.onInsertComplete();
        }
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        if(listener != null){
            listener.onDeleteComplete();
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        if(listener != null){
            listener.onUpdateComplete();
        }
    }
}

