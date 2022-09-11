package com.wuda.wuxue.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Announcement;
import com.wuda.wuxue.bean.BaseInfo;
import com.wuda.wuxue.bean.Lecture;
import com.wuda.wuxue.bean.Movie;
import com.wuda.wuxue.db.InfoDBUtility;
import com.wuda.wuxue.network.AnnouncementNetwork;
import com.wuda.wuxue.network.LectureNetwork;
import com.wuda.wuxue.network.MovieNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.toolkit.ToolActivity;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class InfoSyncIntentService extends IntentService {
    private static final String TAG = "INFO_SYNC_SERVICE";
    private static final String CHANNEL_NAME = "消息订阅";
    private final String CHANNEL_ID = "1";

    private final List<BaseInfo> allInfo = new ArrayList<>();
    // status: -1: 失败，0: 请求中，1: 成功
    private final int[] queryStatus = {0, 0, 0};
    Set<String> selected_items;

    NotificationManager notificationManager;

    public InfoSyncIntentService() {
        super(TAG);
    }

    public void onCreate() {
        super.onCreate();

        selected_items = SharePreferenceManager.loadStringSet(SharePreferenceManager.SUBSCRIBE_INFO_SELECTED_ITEMS);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        startForeground(1, builderNotification(CHANNEL_NAME, "同步中", null, -1).build());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (selected_items.contains(BaseInfo.CATEGORY_ANNOUNCEMENT)) {
            AnnouncementNetwork.requestAnnouncementList(-1, new ResponseHandler<List<Announcement>>() {
                @Override
                public void onHandle(ResponseResult<List<Announcement>> result) {
                    if (result.getData() != null)
                        allInfo.addAll(result.getData());
                    queryStatus[0] = 1;
                    handleAllInfo();
                }
            });
        } else {
            queryStatus[0] = -1;
        }

        if (selected_items.contains(BaseInfo.CATEGORY_MOVIE)) {
            MovieNetwork.requestMovieList(1, new ResponseHandler<List<Movie>>() {
                @Override
                public void onHandle(ResponseResult<List<Movie>> result) {
                    if (result.getData() != null)
                        allInfo.addAll(result.getData());
                    queryStatus[1] = 1;
                    handleAllInfo();
                }
            });
        } else {
            queryStatus[1] = -1;
        }

        if (selected_items.contains(BaseInfo.CATEGORY_LECTURE)) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            Date startDate = new Date();
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, 6);
            Date endDate = calendar.getTime();
            LectureNetwork.requestLectures(sdf.format(startDate), sdf.format(endDate), new ResponseHandler<List<Lecture>>() {
                @Override
                public void onHandle(ResponseResult<List<Lecture>> result) {
                    if (result.getData() != null)
                        allInfo.addAll(result.getData());
                    queryStatus[2] = 1;
                    handleAllInfo();
                }
            });
        } else {
            queryStatus[2] = -1;
        }
    }

    private void handleAllInfo() {

        for (int status: queryStatus) {
            // 等待所有请求结束
            if (status == 0) return;
        }

        List<String> history = InfoDBUtility.queryInfoId();

        Set<String> idSet = new HashSet<>(history);
        List<BaseInfo> newInfo = new ArrayList<>();
        for (BaseInfo info: allInfo) {
            if (!idSet.contains(info.getUniqueId())) {
                newInfo.add(info);
            }
        }
        if (history.size() > 150) {  // 定量清理
            InfoDBUtility.clearAllInfoId();
            InfoDBUtility.saveInfoId(allInfo);
        }

        if (!newInfo.isEmpty()) {
            InfoDBUtility.saveInfoId(newInfo);

            for (int idx=0; idx<newInfo.size(); idx++) {
                BaseInfo info = newInfo.get(idx);
                if (info.getCategory().equals(BaseInfo.CATEGORY_ANNOUNCEMENT)) {
                    notificationManager.notify(idx, builderNotification("通知公告", info.title, info, idx).build());
                } else if (info.getCategory().equals(BaseInfo.CATEGORY_MOVIE)) {
                    notificationManager.notify(idx, builderNotification("梅操电影", info.title, info, idx).build());
                } else if (info.getCategory().equals(BaseInfo.CATEGORY_LECTURE)) {
                    notificationManager.notify(idx, builderNotification("论坛讲座", info.title, info, idx).build());
                }
            }
        }

        stopForeground(false);
    }

    private NotificationCompat.Builder builderNotification(String title, String content, BaseInfo info, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
        }
        // 点击跳转的页面
        Intent intent = new Intent(this, ToolActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("tool", info);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, "1")
                .setContentText(content)
                .setContentTitle(title)
                .setSubText(CHANNEL_NAME)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
    }
}