package com.wuda.wuxue.ui.course;

import com.wuda.wuxue.bean.CourseScore;
import com.wuda.wuxue.db.CourseDBUtility;
import com.wuda.wuxue.network.GraduateCourseNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.List;

public class ScoreStatisticViewModel extends BaseResponseViewModel<List<CourseScore>> {
    public void requestGraduateScore() {
        GraduateCourseNetwork.requestGraduateScore(new ResponseHandler<List<CourseScore>>() {
            @Override
            public void onHandle(ResponseResult<List<CourseScore>> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                    CourseDBUtility.saveCourseScore(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void queryScore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CourseScore> scoreList = CourseDBUtility.queryCourseScore();
                getSuccessResponse().postValue(scoreList);
            }
        }).start();
    }
}
