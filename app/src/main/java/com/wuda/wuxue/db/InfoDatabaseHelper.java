package com.wuda.wuxue.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class InfoDatabaseHelper extends SQLiteOpenHelper {
    // 历史同步的信息

    static String DB_NAME = "Info";
    static String SYNC_TABLE = "Sync";

    private final String createSyncTable = "create table " + SYNC_TABLE + "(" +
            "category text," +
            "uniqueId text," +
            "PRIMARY KEY(category, uniqueId)" +
            ")";

    public static InfoDatabaseHelper getInstance(Context context) {
        return new InfoDatabaseHelper(context, DB_NAME, null, 1);
    }

    public InfoDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSyncTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
