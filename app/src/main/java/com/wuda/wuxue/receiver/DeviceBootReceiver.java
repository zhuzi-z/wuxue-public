package com.wuda.wuxue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.Set;


public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // 开机后重新注册定时器
            Set<String> times = SharePreferenceManager.loadStringSet(SharePreferenceManager.SUBSCRIBE_INFO_TIME_LIST);
            InfoAlarmReceiver.registerAlarm(times);
        }
    }
}