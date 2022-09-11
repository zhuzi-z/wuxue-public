package com.wuda.wuxue.util;

import android.graphics.Color;
import android.util.Pair;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class CourseUtility {
    // 每节课的上课时间
    public static List<Pair<String, String>> getTimeList() {
        return new ArrayList<>(Arrays.asList(
                Pair.create("08:00", "08:45"),
                Pair.create("08:50", "09:35"),
                Pair.create("09:50", "10:35"),
                Pair.create("10:40", "11:25"),
                Pair.create("11:30", "12:15"),
                Pair.create("14:05", "14:50"),
                Pair.create("14:55", "15:40"),
                Pair.create("15:45", "16:30"),
                Pair.create("16:40", "17:25"),
                Pair.create("17:30", "18:15"),
                Pair.create("18:30", "19:15"),
                Pair.create("19:20", "20:05"),
                Pair.create("20:10", "20:55")
        ));
    }

    // 随机的十六进制颜色值
    public static String randomHexColor() {
        StringBuilder hexColor = new StringBuilder();
        hexColor.append('#');
        for (int i=0; i<6; ++i) {
            hexColor.append(Integer.toHexString(new Random().nextInt(16)));
        }
        return hexColor.toString().toUpperCase();
    }

    public static String randomLightHexColor() {
        final int base = 255, max = 155;
        Random random = new Random();
        final int red = base - random.nextInt(max);
        final int green = base - random.nextInt(max);
        final int blue = base - random.nextInt(max);
        int color = Color.rgb(red, green, blue);
        return String.format("#%X", 0xFFFFFF & color);
    }

    // 成绩转绩点
    public static float toGradePoint(float score) {
        if (score >= 90) return 4.0f;
        else if (score >= 85) return 3.7f;
        else if (score >= 82) return 3.3f;
        else if (score >= 78) return 3.0f;
        else if (score >= 75) return 2.7f;
        else if (score >= 72) return 2.3f;
        else if (score >= 68) return 2.0f;
        else if (score >= 64) return 1.5f;
        else if (score >= 60) return 1.0f;
        else return 0.0f;
    }

    // 成绩转等级（研究生）
    public static String toGrade(float score) {
        if (score >= 96) return "A+";
        else if (score >= 90) return "A";
        else if (score >= 85) return "A-";
        else if (score >= 80) return "B+";
        else if (score >= 75) return "B";
        else if (score >= 70) return "B-";
        else if (score >= 67) return "C+";
        else if (score >= 63) return "C";
        else if (score >= 60) return "C-";
        else return "D";
    }
}
