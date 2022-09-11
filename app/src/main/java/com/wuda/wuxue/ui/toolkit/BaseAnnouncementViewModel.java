package com.wuda.wuxue.ui.toolkit;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.Announcement;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAnnouncementViewModel extends BaseResponseViewModel<List<Announcement>> {
    private MutableLiveData<String> content;

    public MutableLiveData<String> getContent() {
        if (content == null) {
            content = new MutableLiveData<>();
        }
        return content;
    }

    public BaseAnnouncementViewModel() {
        data = new ArrayList<>();
    }

    public abstract void requestAnnouncementList(int page);

    public abstract void requestContent(Announcement announcement);

    public abstract void loadMore();
}
