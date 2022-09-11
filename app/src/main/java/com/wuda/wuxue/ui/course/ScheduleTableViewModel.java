package com.wuda.wuxue.ui.course;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.Timetable;
import com.wuda.wuxue.db.CourseDBUtility;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleTableViewModel extends ViewModel {
    private Date startDate;
    private MutableLiveData<List<Course>> courseList;

    public ScheduleTableViewModel() {
        update();
    }

    public MutableLiveData<List<Course>> getCourseList() {
        if (courseList == null) {
            courseList = new MutableLiveData<>();
        }
        return courseList;
    }

    public int getCurrentWeek() {
        Date today = Calendar.getInstance().getTime();
        long days = (today.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        return  (int)days / 7 + 1;
    }

    public void update() {
        // 添加或修改了数据
        int id = SharePreferenceManager.loadInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID);
        List<Timetable> timetableList = CourseDBUtility.queryTimetable();
        for (Timetable timetable: timetableList) {
            if (timetable.getId() == id) {
                startDate = timetable.startDate();
                break;
            }
        }
        // 没有课表
        if (startDate == null) {
            startDate = Calendar.getInstance().getTime();
        }
        getCourseList().postValue(CourseDBUtility.queryCourseSchedule(id));
    }
}
