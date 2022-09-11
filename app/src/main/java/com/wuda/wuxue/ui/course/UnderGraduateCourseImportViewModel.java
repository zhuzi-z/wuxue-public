package com.wuda.wuxue.ui.course;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.CourseScore;
import com.wuda.wuxue.db.CourseDBUtility;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.network.UnderGraduateCourseNetwork;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.List;
import java.util.Map;

import okhttp3.FormBody;

public class UnderGraduateCourseImportViewModel extends BaseResponseViewModel<List<Course>> {

    private MutableLiveData<List<CourseScore>> scoreResponse;

    public MutableLiveData<List<CourseScore>> getScoreResponse() {
        if (scoreResponse == null)
            scoreResponse = new MutableLiveData<>();
        return scoreResponse;
    }

    public void requestCourseList(String url, Map<String, String> form, Map<String, String> header) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (String key: form.keySet()) {
            formBuilder.add(key, form.get(key));
        }

        UnderGraduateCourseNetwork.requestCourseList(url, formBuilder.build(), header, new ResponseHandler<List<Course>>() {
            @Override
            public void onHandle(ResponseResult<List<Course>> result) {
                if (result.isSuccess()) {
                    List<Course> courseList = result.getData();
                    int tableId = SharePreferenceManager.loadInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID);
                    for (Course course : courseList) {
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

    public void requestScoreList(String url, Map<String, String> header) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("xnm", ""); // 学年
        formBuilder.add("xqm", ""); // 学期
        formBuilder.add("queryModel.showCount", "300"); // 最大条数

        UnderGraduateCourseNetwork.requestScoreList(url, formBuilder.build(), header, new ResponseHandler<List<CourseScore>>() {
            @Override
            public void onHandle(ResponseResult<List<CourseScore>> result) {
                if (result.isSuccess()) {
                    getScoreResponse().postValue(result.getData());
                    CourseDBUtility.saveCourseScore(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }
}
