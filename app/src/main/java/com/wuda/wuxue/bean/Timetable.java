package com.wuda.wuxue.bean;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timetable implements Serializable {
    // 课程表表信息： 表名（可自定义），以及开学日期
    private int id;
    private String name;
    private String startDate;

    public Timetable(String name, String startDate) {
        this.name = name;
        this.startDate = startDate;
    }

    public Timetable(int id, String name, String startDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (name.isEmpty())
            return "课表" + Integer.valueOf(id).toString();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public Date startDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
