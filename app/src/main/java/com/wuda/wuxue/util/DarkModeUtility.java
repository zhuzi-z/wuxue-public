package com.wuda.wuxue.util;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class DarkModeUtility {
    public static boolean isDarkMode(Context context) {
        // 当前是否为夜间模式
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            int applicationUiMode = context.getResources().getConfiguration().uiMode;
            int systemMode = applicationUiMode & Configuration.UI_MODE_NIGHT_MASK;
            return systemMode == Configuration.UI_MODE_NIGHT_YES;
        } else {
            return nightMode == AppCompatDelegate.MODE_NIGHT_YES;
        }
    }
}
