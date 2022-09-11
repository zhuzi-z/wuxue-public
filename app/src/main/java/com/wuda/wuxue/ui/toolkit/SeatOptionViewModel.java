package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.bean.Seat;
import com.wuda.wuxue.network.LibSeatNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeatOptionViewModel extends SeatViewModel {

    public SeatOptionViewModel() {
        data = new ArrayList<>();
    }

    public void requestSeats(Map<String, String> param, String date) {

        param.put("offset", Integer.valueOf(totalPage).toString());
        param.put("date", date);

        LibSeatNetwork.requestSeatsByOption(param, new ResponseHandler<List<Seat>>() {
            @Override
            public void onHandle(ResponseResult<List<Seat>> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                    data.addAll(result.getData());
                    // totalPage => 下一页，递增，结束为-1
                    totalPage = result.getTotalPages();
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });

    }

    @Override
    public boolean hasMore() {
        return totalPage != -1;
    }
}
