package com.wuda.wuxue.ui.toolkit;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.SchoolCalendar;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.network.SchoolCalendarNetwork;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.List;

public class SchoolCalendarViewModel extends BaseResponseViewModel<String> {
    private MutableLiveData<List<SchoolCalendar>> calendarList;

    public MutableLiveData<List<SchoolCalendar>> getCalendarList() {
        if (calendarList == null) {
            calendarList = new MutableLiveData<>();
        }
        return calendarList;
    }

    public void requestCalendarList() {
        SchoolCalendarNetwork.requestCalendarList(new ResponseHandler<List<SchoolCalendar>>() {
            @Override
            public void onHandle(ResponseResult<List<SchoolCalendar>> result) {
                if (result.isSuccess()) {
                    if (data==null || data.isEmpty()) {
                        // 首次启动时为空，自动请求最新年份
                        requestCalendarContent(result.getData().get(0));
                        return;
                    }
                    calendarList.postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void requestCalendarContent(SchoolCalendar calendar) {

        SchoolCalendarNetwork.requestCalendarContent(calendar, new ResponseHandler<String>() {
            @Override
            public void onHandle(ResponseResult<String> result) {
                if (result.isSuccess()) {
                    SharePreferenceManager.storeString("calendar_html", result.getData());
                    queryCalendarContent();
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void queryCalendarContent() {

        data = SharePreferenceManager.loadString("calendar_html");

        if (!data.isEmpty()) {
            getSuccessResponse().postValue(data);
        } else {
            // 本地为空时在线获取
            requestCalendarList();
        }

    }
}
