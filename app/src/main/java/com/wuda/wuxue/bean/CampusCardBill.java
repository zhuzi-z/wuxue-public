package com.wuda.wuxue.bean;

public class CampusCardBill {
    String time;  // 交易时间
    String place;  // 窗口
    Float tranAmt;  // 交易金额
    Float cardBal;  // 余额

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Float getTranAmt() {
        return tranAmt;
    }

    public void setTranAmt(Float tranAmt) {
        this.tranAmt = tranAmt;
    }

    public Float getCardBal() {
        return cardBal;
    }

    public void setCardBal(Float cardBal) {
        this.cardBal = cardBal;
    }
}
