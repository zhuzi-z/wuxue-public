package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.bean.Lecture;
import com.wuda.wuxue.network.LectureNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.List;

public class LectureViewModel extends BaseResponseViewModel<List<Lecture>> {
    String start;
    String end;
    public void requestLectures() {
        LectureNetwork.requestLectures(start, end, new ResponseHandler<List<Lecture>>() {
            @Override
            public void onHandle(ResponseResult<List<Lecture>> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                } else
                    getFailResponse().postValue(result);
            }
        });
    }
}
