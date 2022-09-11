package com.wuda.wuxue.bean;

public class SeatLocalHistory {
    // 本地的预约记录
    String time;
    Seat seat;

    public SeatLocalHistory(String time, Seat seat) {
        this.time = time;
        this.seat = seat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
