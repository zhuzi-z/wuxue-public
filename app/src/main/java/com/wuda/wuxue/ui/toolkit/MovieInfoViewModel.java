package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.network.MovieNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

public class MovieInfoViewModel extends BaseResponseViewModel<String> {
    public void requestStoryline(String id) {
        MovieNetwork.requestStoryLine(id, new ResponseHandler<String>() {
            @Override
            public void onHandle(ResponseResult<String> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }
}
