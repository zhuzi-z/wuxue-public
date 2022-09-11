package com.wuda.wuxue.bean;

import com.wuda.wuxue.util.CourseUtility;

public class CourseScore {
    String name;
    String semester;
    float credit;
    float score;
    float gradePoint;
    String type;

    public CourseScore(String name, String semester, float credit, float score, String type) {
        // 课程名，学期，学分，分数，类型
        this.name = name;
        this.semester = semester;
        this.credit = credit;
        this.score = score;
        this.type = type;
        // 绩点：统计平均绩点时需要使用
        this.gradePoint = CourseUtility.toGradePoint(score);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(float gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}