package com.example.sleeptracker.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sleepInfo.db";
    private static final String SLEEP_TABLE_NAME = "student_info";
    private static final String START_TIME_COLUMN = "startTime";
    private static final String END_TIME_COLUMN = "endTime";

    private static final String CREATE_SLEEP_INFO_TABLE_QUERY = "create table " + SLEEP_TABLE_NAME
            + "(" + START_TIME_COLUMN + " text primary key not null, "
            + END_TIME_COLUMN + " text)";
    private static final String DROP_QUERY = "DROP TABLE IF EXISTS " + SLEEP_TABLE_NAME;

    private static DBHelper INSTANCE;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static DBHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBHelper(context);
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SLEEP_INFO_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_QUERY);
        onCreate(db);
    }

    public boolean insertIntoSleepInfo(String from, String to) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(START_TIME_COLUMN, from);
            contentValues.put(END_TIME_COLUMN, to);

            db.insertWithOnConflict(SLEEP_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
        return true;
    }

    public void deleteAll() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(SLEEP_TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    public List<String> getAll() {
        ArrayList<String> array_list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor res;
        try {
            db = this.getReadableDatabase();
            res = db.query(true,
                    SLEEP_TABLE_NAME,
                    new String[]{START_TIME_COLUMN, END_TIME_COLUMN},
                    null,
                    null,
                    null,
                    null,
                    START_TIME_COLUMN,
                    null);
            res.moveToFirst();
            while (!res.isAfterLast()) {
                array_list.add(res.getString(res.getColumnIndex(START_TIME_COLUMN)) + " to " +
                        res.getString(res.getColumnIndex(END_TIME_COLUMN)));
                res.moveToNext();
            }
        } finally {
            db.close();
        }
        return array_list;
    }


}
