package com.wuda.wuxue.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CourseDatabaseHelper extends SQLiteOpenHelper {
    // 课表相关：课程表 / 每个表内的课程 / 课程成绩

    private final String createTimeTable = "create table timetable (" +
            "id integer primary key autoincrement," +
            "name text," +
            "startDate text)";

    private final String createScheduleTable = "create table schedule (" +
            "id integer primary key autoincrement," +
            "courseId text," +
            "name text," +
            "day integer," +
            "room text," +
            "teacher text," +
            "startNode integer," +
            "endNode integer," +
            "startWeek integer," +
            "endWeek integer," +
            "type integer," +
            "credit real, " +
            "color text," +
            "tableId integer)";

    private final String createScoreTable = "create table score (" +
            "id integer primary key autoincrement," +
            "name text," +
            "semester text," +
            "credit real," +
            "score real," +
            "type text)";

    public static CourseDatabaseHelper getInstance(Context context) {
        return new CourseDatabaseHelper(context, "Course", null, 1);
    }

    public CourseDatabaseHelper(@Nullable Context context, @Nullable String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createScheduleTable);
        db.execSQL(createScoreTable);
        db.execSQL(createTimeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
