package com.wuda.wuxue.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LibSeatDatabaseHelper extends SQLiteOpenHelper {
    // 图书馆座位预约的各种选项以及历史记录

    public static final String DB_NAME = "LibSeat";
    public static final String BUILDING_TABLE = "building";
    public static final String ROOM_TABLE = "room";
    public static final String DURATION_TABLE = "duration";
    public static final String TIME_TABLE = "time";  // start time and end time
    public static final String WINDOW_TABLE = "window";
    public static final String POWER_TABLE = "power";
    public static final String ORDER_HISTORY_TABLE = "history";

    private final String createBuildingTable = "create table " + BUILDING_TABLE + " (value text PRIMARY KEY, name text)";
    private final String createRoomTable = "create table " + ROOM_TABLE + " (value text PRIMARY KEY, building text, name text)";
    private final String createDurationTable = "create table " + DURATION_TABLE + " (value text PRIMARY KEY, name text)";
    private final String createTimeTable = "create table " + TIME_TABLE + " (value text PRIMARY KEY, name text)";
    private final String createWindowTable = "create table " + WINDOW_TABLE + " (value text PRIMARY KEY, name text)";
    private final String createPowerTable = "create table " + POWER_TABLE + " (value text PRIMARY KEY, name text)";
    private final String createHistoryTable = "create table " + ORDER_HISTORY_TABLE + " (id integer PRIMARY KEY AUTOINCREMENT, time text, seat_room text, seat_id text, seat_num text)";

    public static LibSeatDatabaseHelper getInstance(Context context) {
        return new LibSeatDatabaseHelper(context, DB_NAME, null, 1);
    }

    public LibSeatDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createBuildingTable);
        db.execSQL(createRoomTable);
        db.execSQL(createDurationTable);
        db.execSQL(createTimeTable);
        db.execSQL(createWindowTable);
        db.execSQL(createPowerTable);
        db.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
