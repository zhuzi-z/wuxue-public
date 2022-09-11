package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.bean.Movie;
import com.wuda.wuxue.network.MovieNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.ArrayList;
import java.util.List;

public class MovieViewModel extends BaseResponseViewModel<List<Movie>> {

    public MovieViewModel() {
        data = new ArrayList<>();
    }

    public void requestMovieList(int page) {
        MovieNetwork.requestMovieList(page, new ResponseHandler<List<Movie>>() {
            @Override
            public void onHandle(ResponseResult<List<Movie>> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                    data.addAll(result.getData());
                    totalPage = result.getTotalPages();
                    currentPage = page;
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void loadMore() {
        requestMovieList(currentPage + 1);
    }
}
