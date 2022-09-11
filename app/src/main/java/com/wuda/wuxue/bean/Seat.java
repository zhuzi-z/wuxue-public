package com.wuda.wuxue.bean;

public class Seat implements Tool {
    String id;
    // 条件选择
    String room;
    // 行号和列号：布局选座构建
    int row;
    int col;
    String no;  // 座位序号
    int type;  // 0 - 3 bits：使用状态，4 - 7 bits:座位类型
               // 自选 free free_both(window|power) free_power （绿） using(灰) order(黄)，leave
               // 布局 idle idle_both idle_window idle_power(绿) inuse（红） ... leave(暂离，黄) agreement(被预约，浅灰) noUsre(不可用，深灰)
    public static final int FREE = 0x00, USING = 0x10, ORDERED = 0x20, LEFT = 0x30, DISABLE = 0x40;
    public static final int NOTHING = 0x00, POWER = 0x01, WINDOW = 0x02, POWER_WINDOW = 0x03;

    public Seat(String id, String room, String no, int type) {
        this.id = id;
        this.room = room;
        this.no = no;
        this.type = type;
    }

    public Seat(String id, int row, int col, String no, int type) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.no = no;
        this.type = type;
    }

    public String getId() {
        if (id.startsWith("seat_")) {
            return id.substring(5);
        }
        return id;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoom() {
        return room;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getNo() {
        return no;
    }

    public int getType() {
        return type;
    }

    @Override
    public String getToolName() {
        return "预约";
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return null;
    }
}
