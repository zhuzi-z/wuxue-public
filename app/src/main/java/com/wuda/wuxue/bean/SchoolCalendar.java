package com.wuda.wuxue.bean;

import com.wuda.wuxue.network.ServerURL;

public class SchoolCalendar {
    // 年份名
    String name;
    String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return ServerURL.CALENDAR_UC + "/xl/" + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
