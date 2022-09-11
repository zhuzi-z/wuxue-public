package com.wuda.wuxue.ui.course;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wuda.wuxue.bean.Timetable;
import com.wuda.wuxue.db.CourseDBUtility;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ScheduleManagerViewModel extends ViewModel {
    MutableLiveData<List<Timetable>> timetable;

    public void createTimeTable() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(Calendar.getInstance().getTime());

        int tableId = CourseDBUtility.insertTimeTable(new Timetable("", date));
        SharePreferenceManager.storeInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID, tableId);
    }


    public MutableLiveData<List<Timetable>> getTimetable() {
        if (timetable == null)
            timetable = new MutableLiveData<>();
        return timetable;
    }

    public void queryTimetable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                timetable.postValue(CourseDBUtility.queryTimetable());
            }
        }).start();
    }

    public void deleteTimetable(Timetable table) {
        CourseDBUtility.deleteCourseByTableId(table.getId());
        CourseDBUtility.deleteTimeTable(table);
    }

    public void updateTimetable(Timetable table) {
        CourseDBUtility.updateTimetable(table);
    }
}
