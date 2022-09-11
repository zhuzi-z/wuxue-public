package com.wuda.wuxue.bean;

public class OptionPair {
    // 选项对（图书馆时长等）
    public static String SEAT_BUILDING = "SEAT_BUILDING", SEAT_ROOM = "SEAT_ROOM",
            SEAT_DURATION = "SEAT_DURATION", SEAT_START_MIN = "SEAT_START_MIN", SEAT_END_MIN = "SEAT_END_MIN",
            SEAT_POWER = "SEAT_POWER", SEAT_WINDOW = "SEAT_WINDOW",
            SYNCHRONIZER_TOKEN = "SYNCHRONIZER_TOKEN", SYNCHRONIZER_URI = "SYNCHRONIZER_URI";

    String value;
    String name;

    public OptionPair(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
