package com.wuda.wuxue;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.wuda.wuxue.util.SharePreferenceManager;

public class WuXueApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        int nightMode = SharePreferenceManager.loadInteger(SharePreferenceManager.MIME_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    public static Context getContext() {
        return context;
    }
}
