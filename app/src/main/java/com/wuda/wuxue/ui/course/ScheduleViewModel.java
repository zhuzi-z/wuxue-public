package com.wuda.wuxue.ui.course;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.Timetable;
import com.wuda.wuxue.db.CourseDBUtility;
import com.wuda.wuxue.network.GraduateCourseNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleViewModel extends BaseResponseViewModel<List<Course>> {

    private MutableLiveData<Timetable> currentTimeTable;
    private Integer currentWeek = null;

    public MutableLiveData<Timetable> getCurrentTimeTable() {
        if (currentTimeTable == null) {
            currentTimeTable = new MutableLiveData<>();
            reloadCurrentTable();
        }
        return currentTimeTable;
    }

    public void reloadCurrentTable() {
        int id = SharePreferenceManager.loadInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID);
        List<Timetable> timetableList = CourseDBUtility.queryTimetable();
        for (Timetable timetable: timetableList) {
            if (timetable.getId() == id) {
                currentTimeTable.postValue(timetable);
                currentWeek = null;
                return;
            }
        }
        currentWeek = null;
        currentTimeTable.postValue(null);
    }

    public void requestGraduateSchedule() {
        GraduateCourseNetwork.requestGraduateSchedule(new ResponseHandler<List<Course>>() {
            @Override
            public void onHandle(ResponseResult<List<Course>> result) {
                if (result.isSuccess()) {
                    List<Course> courseList = result.getData();
                    int tableId = SharePreferenceManager.loadInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID);
                    for (Course course: courseList) {
                        course.setTableId(tableId);
                    }
                    CourseDBUtility.saveCourseSchedule(courseList);
                    getSuccessResponse().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void createTimeTable() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(Calendar.getInstance().getTime());

        int tableId = CourseDBUtility.insertTimeTable(new Timetable("", date));
        SharePreferenceManager.storeInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID, tableId);

        reloadCurrentTable();
    }

    public int getCurrentWeek() {
        if (currentWeek == null) {
            Date today = Calendar.getInstance().getTime();
            long days = (today.getTime() - getCurrentTimeTable().getValue().startDate().getTime()) / (24 * 60 * 60 * 1000);
            currentWeek = (int)days / 7 + 1;
        }
        return currentWeek;
    }
}
