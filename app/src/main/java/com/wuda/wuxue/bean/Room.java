package com.wuda.wuxue.bean;

public class Room extends OptionPair {
    // 图书馆座位预约的房间
    String building;

    public Room(String value, String building, String name) {
        super(value, name);
        this.building = building;
    }

    public String getBuilding() {
        return building;
    }
}
