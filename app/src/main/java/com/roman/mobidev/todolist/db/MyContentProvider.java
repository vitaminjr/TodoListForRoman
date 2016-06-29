package com.roman.mobidev.todolist.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.roman.mobidev.todolist.helpers.TaskFields;


public class MyContentProvider extends ContentProvider {

    static final String DB_NAME = "todolistDB";
    static final int DB_VERSION = 1;

    // Таблица
    static final String TASK_TABLE = "tasks";

    // Поля
//    static final String TASKS_ID = "_id";
//    static final String TASKS_NAME = "NAME";
//    static final String TASKS_EMAIL = "email";

    // Скрипт создания таблицы
    static final String DB_CREATE = "create table " + TASK_TABLE + "("
            + TaskFields._id.name() + " integer primary key, "
            + TaskFields.NAME.name() + " text, "
            + TaskFields.DESCRIPTION.name() + " text, "
            + TaskFields.EXPIRE.name() + " text, "
            + TaskFields.STATUS.name() + " text" + ");";

    // // Uri
    // authority
    static final String AUTHORITY = "com.roman.mobidev.todolist";

    // path
    static final String TASKS_PATH = TASK_TABLE;

    // Общий Uri
    public static final Uri TASKS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TASKS_PATH);

    // Типы данных
    // набор строк
    static final String TASKS_CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd."
                    + AUTHORITY + "." + TASKS_PATH;

    // одна строка
    static final String TASKS_CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd."
                    + AUTHORITY + "." + TASKS_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_TASKS = 1;

    // Uri с указанным ID
    static final int URI_TASKS_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TASKS_PATH, URI_TASKS);
        uriMatcher.addURI(AUTHORITY, TASKS_PATH + "/#", URI_TASKS_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public MyContentProvider() {

    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    private String formatSelection(String selection, String id) {
        if (TextUtils.isEmpty(selection)) {
            selection = TaskFields._id + " = " + id;
        } else {
            selection = selection + " AND " + TaskFields._id + " = " + id;
        }
        return selection;
    }

    private String handelUri(Uri uri, String selection) {
        switch (uriMatcher.match(uri)) {
            case URI_TASKS:

                break;
            case URI_TASKS_ID:
                String id = uri.getLastPathSegment();
                selection = formatSelection(selection, id);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return selection;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_TASKS:
                return TASKS_CONTENT_TYPE;
            case URI_TASKS_ID:
                return TASKS_CONTENT_ITEM_TYPE;
        }
        return null;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        selection = handelUri(uri, selection);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TASK_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), TASKS_CONTENT_URI);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_TASKS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        //String rowID = String.valueOf(db.insert(TASK_TABLE, null, values));
        Uri resultUri = null;
        try {
            long   rowID = db.insert(TASK_TABLE, null, values);
            resultUri = ContentUris.withAppendedId(TASKS_CONTENT_URI, rowID);
        }
        catch (SQLiteConstraintException ex)
        {
            ex.printStackTrace();
            Log.d("Exception","Дані " + ex.getMessage());
//            Toast.makeText(getContext(), "Дані вже загружені" + ex.getMessage(),Toast.LENGTH_LONG).show();

        }


        //Uri resultUri = Uri.parse(TASKS_CONTENT_URI + "/" + rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        selection = handelUri(uri, selection);
        db = dbHelper.getWritableDatabase();
        int count = db.update(TASK_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        selection = handelUri(uri, selection);
        db = dbHelper.getWritableDatabase();
        int count = db.delete(TASK_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }




    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
//            ContentValues cv = new ContentValues();
//            for (int i = 1; i <= 3; i++) {
//                cv.put(TASKS_NAME, "NAME " + i);
//                cv.put(TASKS_EMAIL, "email " + i);
//                db.insert(TASKS_TABLE, null, cv);
//            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
