package com.wuda.wuxue.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.wuda.wuxue.WuXueApplication;

import java.util.HashSet;
import java.util.Set;

public class SharePreferenceManager {
    // Cookie
    public static String CAS_COOKIE = "cas_cookie";
    // 课表
    public static String SCHEDULE_CURRENT_TABLE_ID = "schedule_current_table_id";
    // 消息订阅
    public static String SUBSCRIBE_INFO_SELECTED_ITEMS = "subscribe_info_selected_items";
    public static String SUBSCRIBE_INFO_TIME_LIST = "subscribe_info_time_list";
    public static String SUBSCRIBE_INFO_LAST_ALARMS = "subscribe_info_last_alarms";
    // 座位预约（楼层）
    public static String LIB_SEAT_MAP_BUILDING = "seat_map_building";
    public static String LIB_SEAT_MAP_ROOM = "seat_map_room";
    // 座位预约（选项）
    public static String LIB_SEAT_OPTION_BUILDING = "seat_option_building";
    public static String LIB_SEAT_OPTION_ROOM = "seat_option_room";
    public static String LIB_SEAT_OPTION_DURATION = "seat_option_duration";
    public static String LIB_SEAT_OPTION_START_MIN = "seat_option_start_min";
    public static String LIB_SEAT_OPTION_END_MIN = "seat_option_end_min";
    public static String LIB_SEAT_OPTION_POWER = "seat_option_power";
    public static String LIB_SEAT_OPTION_WINDOW = "seat_option_window";
    // 设置
    public static String MIME_NIGHT_MODE = "night_mode";
    public static String MIME_STARTUP = "startup";
    // 首次启动
    public static String APP_FIRST_START = "app_first_start";

    public static String loadString(String key) {
        SharedPreferences pref = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static void storeString(String key, String value) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Integer loadInteger(String key) {
        SharedPreferences pref = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public static Integer loadInteger(String key, int defaultValue) {
        SharedPreferences pref = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        return pref.getInt(key, defaultValue);
    }

    public static void storeInteger(String key, int value) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean loadBoolean(String key) {
        SharedPreferences pref = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    public static boolean loadBoolean(String key, boolean defaultValue) {
        SharedPreferences pref = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        return pref.getBoolean(key, defaultValue);
    }

    public static void storeBoolean(String key, boolean value) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Set<String> loadStringSet(String key) {
        SharedPreferences pref = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        return pref.getStringSet(key, new HashSet<>());
    }

    public static void storeStringSet(String key, Set<String> strings) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = WuXueApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE).edit();
        editor.putStringSet(key, strings);
        editor.apply();
    }
}
