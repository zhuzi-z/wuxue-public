package com.wuda.wuxue.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuda.wuxue.WuXueApplication;
import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.CourseScore;
import com.wuda.wuxue.bean.Timetable;

import java.util.ArrayList;
import java.util.List;

public class CourseDBUtility {
    
    public static int insertTimeTable (Timetable table) {
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into Timetable (name, startDate) " +
                "values(?, ?)", new Object[]{
                "", // getName返回字符串
                table.getStartDate()
        });
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select last_insert_rowid() from Timetable",null);
        if(cursor.moveToFirst())
            return  cursor.getInt(0);
        db.close();
        return -1;
    }

    public static void updateTimetable (Timetable table) {
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update Timetable set name = ? where id = ?", new Object[]{table.getName(), table.getId()});
        db.execSQL("update Timetable set startDate = ? where id = ?", new Object[]{table.getStartDate(), table.getId()});
        db.close();
    }

    public static void deleteTimeTable (Timetable table) {
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from TimeTable where id = ?", new Object[]{table.getId()});
        db.close();
    }

    public static List<Timetable> queryTimetable() {

        List<Timetable> tables = new ArrayList<>();

        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from timetable", null);
        int idIdx = cursor.getColumnIndex("id");
        int nameIdx = cursor.getColumnIndex("name");
        int startDateIdx = cursor.getColumnIndex("startDate");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIdx);
                String name = cursor.getString(nameIdx);
                String startDate = cursor.getString(startDateIdx);
                tables.add(new Timetable(id, name, startDate));
            } while (cursor.moveToNext());
        }
        db.close();

        return tables;
    }

    public static void saveCourseSchedule(List<Course> courseList) {
        if (courseList != null && !courseList.isEmpty()) {

            int tableId = courseList.get(0).getTableId();

            CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("delete from Schedule where tableId = ?", new Object[]{tableId});

            for (Course course: courseList) {
                db.execSQL("insert into Schedule ( name, day, room, teacher, startNode, endNode, startWeek, endWeek, type, credit, color, tableId) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                        course.getName(),
                        course.getDay(),
                        course.getRoom(),
                        course.getTeacher(),
                        course.getStartNode(), course.getEndNode(),
                        course.getStartWeek(), course.getEndWeek(),
                        course.getType(),
                        course.getCredit(),
                        course.getColor(),
                        course.getTableId()
                });
            }

            db.close();
        }
    }

    public static void insertOneCourse(Course course) {
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 如果存在，删除旧的
        db.execSQL("delete from Schedule where id=?", new Object[]{course.getId()});
        // 不需要ID
        db.execSQL("insert into Schedule (name, day, room, teacher, startNode, endNode, startWeek, endWeek, type, credit, color, tableId) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                course.getName(),
                course.getDay(),
                course.getRoom(),
                course.getTeacher(),
                course.getStartNode(), course.getEndNode(),
                course.getStartWeek(), course.getEndWeek(),
                course.getType(),
                course.getCredit(),
                course.getColor(),
                course.getTableId()
        });

        db.close();
    }

    public static List<Course> queryCourseSchedule(int tableId) {
        List<Course> courseList = new ArrayList<>();

        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from schedule where tableId = ?", new String[]{Integer.valueOf(tableId).toString()});
        int idIdx = cursor.getColumnIndex("id");
        int nameIdx = cursor.getColumnIndex("name");
        int dayIdx = cursor.getColumnIndex("day");
        int roomIdx = cursor.getColumnIndex("room");
        int teacherIdx = cursor.getColumnIndex("teacher");
        int startNodeIdx = cursor.getColumnIndex("startNode");
        int endNodeIdx = cursor.getColumnIndex("endNode");
        int startWeekIdx = cursor.getColumnIndex("startWeek");
        int endWeekIdx = cursor.getColumnIndex("endWeek");
        int typeIdx = cursor.getColumnIndex("type");
        int creditIdx = cursor.getColumnIndex("credit");
        int colorIdx = cursor.getColumnIndex("color");
        int tableIdIdx = cursor.getColumnIndex("tableId");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIdx);
                String name = cursor.getString(nameIdx);
                int day = cursor.getInt(dayIdx);
                String room = cursor.getString(roomIdx);
                String teacher = cursor.getString(teacherIdx);
                int startNode = cursor.getInt(startNodeIdx);
                int endNode = cursor.getInt(endNodeIdx);
                int startWeek = cursor.getInt(startWeekIdx);
                int endWeek = cursor.getInt(endWeekIdx);
                int type = cursor.getInt(typeIdx);
                float credit = cursor.getFloat(creditIdx);
                String color = cursor.getString(colorIdx);

                courseList.add(
                        new Course(id, name, day, room, teacher,
                                startNode, endNode, startWeek, endWeek,
                                type, credit, color, tableId)
                );

            } while (cursor.moveToNext());
        }

        db.close();

        return courseList;
    }

    public static void deleteCourse (Course course) {
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from Schedule where id = ?", new Object[]{course.getId()});
        db.close();
    }

    public static void deleteCourseByTableId (int id) {
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("delete from schedule where tableId = ? ", new Object[]{id});
        db.close();
    }

    public static void saveCourseScore(List<CourseScore> scoreList) {
        if (scoreList != null) {
            CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("delete from Score");

            for (CourseScore courseScore : scoreList) {
                db.execSQL("insert into Score (name, semester, credit, score, type) values (?, ?, ?, ?, ?)",
                        new Object[]{
                                courseScore.getName(), courseScore.getSemester(),
                                courseScore.getCredit(), courseScore.getScore(),
                                courseScore.getType()
                        }
                );
            }
            db.close();
        }
    }

    public static List<CourseScore> queryCourseScore() {

        List<CourseScore> scoreList = new ArrayList<>();

        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(WuXueApplication.getContext(), "Course", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from score", null);
        int nameIdx = cursor.getColumnIndex("name");
        int semesterIdx = cursor.getColumnIndex("semester");
        int creditIdx = cursor.getColumnIndex("credit");
        int scoreIdx = cursor.getColumnIndex("score");
        int typeIdx = cursor.getColumnIndex("type");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(nameIdx);
                String semester = cursor.getString(semesterIdx);
                float credit = cursor.getFloat(creditIdx);
                float score = cursor.getFloat(scoreIdx);
                String type = cursor.getString(typeIdx);
                scoreList.add(new CourseScore(name, semester, credit, score, type));
            } while (cursor.moveToNext());
        }
        db.close();

        return scoreList;
    }
}
