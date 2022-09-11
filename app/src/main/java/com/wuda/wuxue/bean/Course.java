package com.wuda.wuxue.bean;

import java.io.Serializable;

public class Course implements Serializable {
    // 项目在数据库表中的唯一ID
    int id;
    // 课程 ID(同一门课有唯一的课程ID，但如果一个星期多个时间段上课会有多个id)
    String courseId; // 暂时不使用
    // 课程名
    String name;
    // 周几
    int day;
    // 教室
    String room;
    String teacher;
    // 开始节 - 结束节
    int startNode;
    int endNode;
    // 开始周 - 结束周
    int startWeek;
    int endWeek;
    // 单双周
    int type;
    float credit;
    // 显示的颜色
    String color;
    // 所属的表ID
    int tableId;

    public static int WEEK_TYPE_ALL = 0, WEEK_TYPE_ODD = 1, WEEK_TYPE_EVEN = 2;

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public Course(String name, int day, String room, String teacher, int startNode, int endNode, int startWeek, int endWeek, int type) {
        this.name = name;
        this.day = day;
        this.room = room;
        this.teacher = teacher;
        this.startNode = startNode;
        this.endNode = endNode;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.type = type;
    }

    public Course(int id, String name, int day, String room, String teacher, int startNode, int endNode, int startWeek, int endWeek, int type, float credit, String color, int tableId) {
        this.id = id;
        this.name = name;
        this.day = day;
        this.room = room;
        this.teacher = teacher;
        this.startNode = startNode;
        this.endNode = endNode;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.type = type;
        this.credit = credit;
        this.color = color;
        this.tableId = tableId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getStartNode() {
        return startNode;
    }

    public void setStartNode(int startNode) {
        this.startNode = startNode;
    }

    public int getEndNode() {
        return endNode;
    }

    public void setEndNode(int endNode) {
        this.endNode = endNode;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
