package com.wuda.wuxue;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.wuda.wuxue.network.HttpClient;
import com.wuda.wuxue.util.SharePreferenceManager;

public class WuXueApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static String cookie;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        cookie = SharePreferenceManager.loadString(SharePreferenceManager.CAS_COOKIE);

        int nightMode = SharePreferenceManager.loadInteger(SharePreferenceManager.MIME_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    public static Context getContext() {
        return context;
    }

    public static void setCookies(String cookie) {
        SharePreferenceManager.storeString(SharePreferenceManager.CAS_COOKIE, cookie);
        WuXueApplication.cookie = cookie;
        // 清除 Cookie
        HttpClient.clearCookieStore();
    }

    public static String getCookieAsString() {
        return cookie;
    }
}
