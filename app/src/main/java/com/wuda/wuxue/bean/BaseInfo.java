package com.wuda.wuxue.bean;

import com.google.gson.annotations.SerializedName;

public abstract class BaseInfo implements Tool {
    // 主要用于消息订阅
    public static String CATEGORY_ANNOUNCEMENT = "ANNOUNCEMENT", CATEGORY_MOVIE = "MOVIE", CATEGORY_LECTURE = "LECTURE";

    protected String category;
    @SerializedName(value = "TITLE", alternate = "title")
    public String title = "";
    @SerializedName("PLAY_TIME")
    public String time = "";
    public boolean read = false;

    public String getCategory() {
        if (category == null)
            return "";
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getRead() {
        return read;
    }

    public void setRead(boolean isRead) {
        this.read = isRead;
    }
    // 分享和在浏览器打开必须！！！
    public abstract String getUrl();
    // unique id，信息存储的键
    public abstract String getUniqueId();

    public boolean isRead() {
        return read;
    }
}
