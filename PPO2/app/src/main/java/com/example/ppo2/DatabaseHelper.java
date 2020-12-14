package com.example.ppo2;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.graphics.Color;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "sequences.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "sequences"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PREPARE = "prepare";
    public static final String COLUMN_WORK = "work";
    public static final String COLUMN_CHILL = "chill";
    public static final String COLUMN_CYCLES = "cycles";
    public static final String COLUMN_SETS = "sets";
    public static final String COLUMN_SETCHILL = "setChill";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_COLOR
                + " INTEGER, " + COLUMN_TITLE
                + " TEXT, " + COLUMN_PREPARE
                + " INTEGER, " + COLUMN_WORK
                + " INTEGER, " + COLUMN_CHILL
                + " INTEGER, " + COLUMN_CYCLES
                + " INTEGER, " + COLUMN_SETS
                + " INTEGER, " + COLUMN_SETCHILL + " INTEGER);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}