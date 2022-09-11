package com.wuda.wuxue.bean;

public enum UserType {
    UNDERGRADUATE(3), GRADUATE(2), FACULTY(0);

    private int value = 0;

    private UserType(int value) {
        this.value = value;
    }

    public static UserType valueOf(int value) {
        switch (value) {
            case 0:
                return FACULTY;
            // 学号
            case 1:
            case 2:
                return GRADUATE;
            case 3:
                return UNDERGRADUATE;
            default:
                return null;
        }
    }

    public int value() {
        return this.value;
    }
}
