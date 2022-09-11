package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.bean.Announcement;
import com.wuda.wuxue.network.AnnouncementNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.network.ServerURL;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementViewModel extends BaseAnnouncementViewModel {
    public AnnouncementViewModel() {
        data = new ArrayList<>();
    }

    public void requestAnnouncementList(int page) {
        currentPage = page;
        AnnouncementNetwork.requestAnnouncementList(page, new ResponseHandler<List<Announcement>>() {
            @Override
            public void onHandle(ResponseResult<List<Announcement>> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                    data.addAll(result.getData());
                    totalPage = result.getTotalPages();
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    @Override
    public void requestContent(Announcement announcement) {
        AnnouncementNetwork.requestAnnouncementContent(announcement.getUrl(), new ResponseHandler<String>() {
            @Override
            public void onHandle(ResponseResult<String> result) {
                if (result.isSuccess()) {
                    getContent().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    @Override
    public boolean isFirstPage() {
        return currentPage == -1;
    }

    public void loadMore() {
        if (currentPage == -1) {
            currentPage = totalPage - 1;
        } else {
            currentPage--;
        }
        requestAnnouncementList(currentPage);
    }

    @Override
    public boolean hasMore() {
        return currentPage != 1;
    }
}
