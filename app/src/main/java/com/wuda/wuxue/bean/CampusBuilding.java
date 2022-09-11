package com.wuda.wuxue.bean;

public class CampusBuilding {
    // 空教室查询
    String id;
    String name;
    String campus;

    public CampusBuilding() {
    }

    public CampusBuilding(String id, String name, String campus) {
        this.id = id;
        this.name = name;
        this.campus = campus;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
