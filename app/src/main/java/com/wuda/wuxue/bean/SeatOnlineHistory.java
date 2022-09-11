package com.wuda.wuxue.bean;

public class SeatOnlineHistory {
    // 在线预约记录
    String title;
    String time;
    // visaId 用于取消预约
    String visaId;
    int state;

    public SeatOnlineHistory(String title, String time, String visaId, int state) {
        this.title = title;
        this.time = time;
        this.visaId = visaId;
        this.state = state;
    }

    public final static int STATE_CANCELED=-1, STATE_NORMAL=0;

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

    public String getVisaId() {
        return visaId;
    }

    public void setVisaId(String visaId) {
        this.visaId = visaId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
