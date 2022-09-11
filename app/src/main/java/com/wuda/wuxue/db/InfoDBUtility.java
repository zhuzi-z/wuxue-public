package com.wuda.wuxue.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuda.wuxue.WuXueApplication;
import com.wuda.wuxue.bean.BaseInfo;

import java.util.ArrayList;
import java.util.List;

public class InfoDBUtility {
    public static List<String> queryInfoId() {
        InfoDatabaseHelper helper = InfoDatabaseHelper.getInstance(WuXueApplication.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        List<String> infoList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from " + InfoDatabaseHelper.SYNC_TABLE, null);
        int uniqueIdIdx = cursor.getColumnIndex("uniqueId");
        if (cursor.moveToFirst()) {
            do {
                String uniqueId = cursor.getString(uniqueIdIdx);
                infoList.add(uniqueId);
            } while (cursor.moveToNext());
        }
        db.close();

        return infoList;
    }

    public static void saveInfoId(List<?> infoList) {
        SQLiteDatabase db = InfoDatabaseHelper.getInstance(WuXueApplication.getContext()).getWritableDatabase();
        for (int i=0; i<infoList.size(); i++) {
            if (infoList.get(i) instanceof BaseInfo) {
                BaseInfo info = (BaseInfo) infoList.get(i);
                // 初始化时可能遇到相同的ID
                db.execSQL("insert or ignore into " + InfoDatabaseHelper.SYNC_TABLE + " (category, uniqueId) values(?, ?)", new Object[]{
                        info.getCategory(),
                        info.getUniqueId()
                });
            }
        }
        db.close();
    }

    public static void clearAllInfoId() {
        SQLiteDatabase db = InfoDatabaseHelper.getInstance(WuXueApplication.getContext()).getWritableDatabase();
        db.execSQL("delete from " + InfoDatabaseHelper.SYNC_TABLE);
        db.close();
    }
}
