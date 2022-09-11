package com.wuda.wuxue.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.wuda.wuxue.WuXueApplication;
import com.wuda.wuxue.service.InfoSyncIntentService;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;

public class InfoAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, InfoSyncIntentService.class));
        } else {
            context.startService(new Intent(context, InfoSyncIntentService.class));
        }
    }

    public static void registerAlarm(Set<String> times) {
        Intent alarmIntent = new Intent(WuXueApplication.getContext(), InfoAlarmReceiver.class);
        AlarmManager manager = (AlarmManager) WuXueApplication.getContext().getSystemService(Context.ALARM_SERVICE);

        // -15 ~ +15 minutes 的扰动（服务端的并发？？？）
        long delta = new Random().nextInt(30 * 60 * 1000) - 15 * 60 * 1000;

        int alarms = 0;
        Calendar calendar = Calendar.getInstance();
        for (String time: times) {
            if (time.equals("")) continue;
            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Date date = calendar.getTime();
            // FLAG_UPDATE_CURRENT,如果已存在则替换已有
            // requestCode：区分不同Intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(WuXueApplication.getContext(), alarms, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, date.getTime() + delta, 24 * 60 * 60 * 1000, pendingIntent);
            alarms++;
        }

        int last_alarms = SharePreferenceManager.loadInteger(SharePreferenceManager.SUBSCRIBE_INFO_LAST_ALARMS);
        // 如果本次的少于上一次，说明部分被取消
        for (int alarm=alarms; alarm<last_alarms; alarm++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(WuXueApplication.getContext(), alarm, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            manager.cancel(pendingIntent);
        }

        SharePreferenceManager.storeInteger(SharePreferenceManager.SUBSCRIBE_INFO_LAST_ALARMS, alarms);
    }
}