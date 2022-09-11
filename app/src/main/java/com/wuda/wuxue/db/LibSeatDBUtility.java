package com.wuda.wuxue.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuda.wuxue.WuXueApplication;
import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.Room;
import com.wuda.wuxue.bean.Seat;
import com.wuda.wuxue.bean.SeatLocalHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibSeatDBUtility {

    public static void saveAllOptionPairs(Map<String, List<OptionPair>> options) {
        saveOptionPairs(options.get(OptionPair.SEAT_BUILDING), LibSeatDatabaseHelper.BUILDING_TABLE);
        saveOptionPairs(options.get(OptionPair.SEAT_DURATION), LibSeatDatabaseHelper.DURATION_TABLE);
        saveOptionPairs(options.get(OptionPair.SEAT_START_MIN), LibSeatDatabaseHelper.TIME_TABLE);
        saveOptionPairs(options.get(OptionPair.SEAT_WINDOW), LibSeatDatabaseHelper.WINDOW_TABLE);
        saveOptionPairs(options.get(OptionPair.SEAT_POWER), LibSeatDatabaseHelper.POWER_TABLE);
    }

    private static void saveOptionPairs(List<OptionPair> optionList, String table) {
        if (optionList !=null && !optionList.isEmpty()) {
            SQLiteDatabase db = LibSeatDatabaseHelper.getInstance(WuXueApplication.getContext()).getWritableDatabase();
            db.execSQL("delete from " + table);

            for (OptionPair option: optionList) {
                db.execSQL("replace into " + table + "(value, name)" + "values(?, ?)",
                        new Object[]{option.getValue(), option.getName()});
            }
            db.close();
        }
    }

    public static Map<String, List<OptionPair>> queryAllOptionPairs() {
        Map<String, List<OptionPair>> options = new HashMap<>();

        options.put(OptionPair.SEAT_BUILDING, queryOptionPairs(LibSeatDatabaseHelper.BUILDING_TABLE));
        options.put(OptionPair.SEAT_DURATION, queryOptionPairs(LibSeatDatabaseHelper.DURATION_TABLE));
        options.put(OptionPair.SEAT_START_MIN, queryOptionPairs(LibSeatDatabaseHelper.TIME_TABLE));
        options.put(OptionPair.SEAT_POWER, queryOptionPairs(LibSeatDatabaseHelper.POWER_TABLE));
        options.put(OptionPair.SEAT_WINDOW, queryOptionPairs(LibSeatDatabaseHelper.WINDOW_TABLE));

        return options;
    }

    private static List<OptionPair> queryOptionPairs(String table) {

        List<OptionPair> optionList = new ArrayList<>();

        SQLiteDatabase db = LibSeatDatabaseHelper.getInstance(WuXueApplication.getContext()).getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from " + table, null);
        int valueIdx = cursor.getColumnIndex("value");
        int nameIdx = cursor.getColumnIndex("name");

        if (cursor.moveToFirst()) {
            do {
                String value = cursor.getString(valueIdx);
                String name = cursor.getString(nameIdx);
                optionList.add(new OptionPair(value, name));
            } while (cursor.moveToNext());
        }
        db.close();

        return optionList;
    }

    public static void saveRooms(List<Room> roomList) {
        if (roomList != null && !roomList.isEmpty()) {
            SQLiteDatabase db = LibSeatDatabaseHelper.getInstance(WuXueApplication.getContext()).getWritableDatabase();
            db.execSQL("delete from " + LibSeatDatabaseHelper.ROOM_TABLE + " where building=?", new Object[]{roomList.get(0).getBuilding()});

            for (Room room: roomList) {
                db.execSQL("replace into " + LibSeatDatabaseHelper.ROOM_TABLE + "(value, building, name)" + "values(?, ?, ?)",
                        new Object[]{room.getValue(), room.getBuilding(), room.getName()});
            }
            db.close();
        }
    }

    public static List<OptionPair> queryRooms(String building) {
        List<OptionPair> roomList = new ArrayList<>();

        SQLiteDatabase db = LibSeatDatabaseHelper.getInstance(WuXueApplication.getContext()).getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from " + LibSeatDatabaseHelper.ROOM_TABLE + " where building=?", new String[]{building});
        int valueIdx = cursor.getColumnIndex("value");
        int nameIdx = cursor.getColumnIndex("name");

        if (cursor.moveToFirst()) {
            do {
                String value = cursor.getString(valueIdx);
                String name = cursor.getString(nameIdx);
                roomList.add(new Room(value, building, name));
            } while (cursor.moveToNext());
        }

        db.close();

        return roomList;
    }

    public static void saveLocalHistory(SeatLocalHistory history) {
        if (history != null) {
            SQLiteDatabase db = LibSeatDatabaseHelper.getInstance(WuXueApplication.getContext()).getWritableDatabase();

            db.execSQL("insert into " + LibSeatDatabaseHelper.ORDER_HISTORY_TABLE + "(time, seat_room, seat_id, seat_num)" + "values(?, ?, ?, ?)",
                    new Object[]{history.getTime(), history.getSeat().getRoom(), history.getSeat().getId(), history.getSeat().getNo()});
            db.close();
        }
    }

    public static List<SeatLocalHistory> queryLocalHistory() {

        List<SeatLocalHistory> historyList = new ArrayList<>();

        SQLiteDatabase db = LibSeatDatabaseHelper.getInstance(WuXueApplication.getContext()).getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from " + LibSeatDatabaseHelper.ORDER_HISTORY_TABLE +  " order by id DESC limit 8", null);
        int timeIdx = cursor.getColumnIndex("time");
        int seatRoomIdx = cursor.getColumnIndex("seat_room");
        int seatIdIdx = cursor.getColumnIndex("seat_id");
        int seatNumIdx = cursor.getColumnIndex("seat_num");

        if (cursor.moveToFirst()) {
            do {
                String time = cursor.getString(timeIdx);
                String seatRoom = cursor.getString(seatRoomIdx);
                String seatID = cursor.getString(seatIdIdx);
                String seatNum = cursor.getString(seatNumIdx);
                Seat seat = new Seat(seatID, seatRoom, seatNum, Seat.FREE);
                historyList.add(new SeatLocalHistory(time, seat));
            } while (cursor.moveToNext());
        }

        db.close();

        return historyList;
    }
}
