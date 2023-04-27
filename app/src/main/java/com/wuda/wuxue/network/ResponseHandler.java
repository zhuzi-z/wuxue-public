package com.wuda.wuxue.network;

public interface ResponseHandler<T> {
    // 异步回调统一接口
    void onHandle(ResponseResult<T> result);
}
