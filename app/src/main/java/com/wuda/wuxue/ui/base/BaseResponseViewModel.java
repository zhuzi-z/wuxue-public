package com.wuda.wuxue.ui.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wuda.wuxue.network.ResponseResult;

public abstract class BaseResponseViewModel<T> extends ViewModel {
    // 一次成功的请求数据（通用，主要用于主响应，其余自行扩展）
    protected MutableLiveData<T> successResponse;
    // 失败的请求，不同的请求失败结果都从这里返回，可以通过ResponseResult的flag进行区分
    protected MutableLiveData<ResponseResult<?>> failResponse;
    // 所有的数据，对于分页请求的数据使用data进行缓存
    protected T data;
    // 页码
    protected int currentPage;
    protected int totalPage;

    public MutableLiveData<T> getSuccessResponse() {
        if (successResponse == null) {
            successResponse = new MutableLiveData<>();
        }
        return successResponse;
    }

    public MutableLiveData<ResponseResult<?>> getFailResponse() {
        if (failResponse == null) {
            failResponse = new MutableLiveData<>();
        }
        return failResponse;
    }

    public T getData() {
        return data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isFirstPage() {
        return currentPage == 1;
    }

    public boolean hasMore() {
        return currentPage <= totalPage;
    }

    public void resetLiveData() {
        getSuccessResponse().setValue(null);
        getFailResponse().setValue(null);
    }

    public void clearFailResponse() {
        // 处理完异常后清空，View中会接收到null数据，需要自行过滤
        failResponse = null;
    }

    public void clearSuccessResponse() {
        // 对于分页数据会同步存储到 data 上，当页面回到栈顶时，该数据可能与 data 中的数据重复
        successResponse = null;
    }
}
