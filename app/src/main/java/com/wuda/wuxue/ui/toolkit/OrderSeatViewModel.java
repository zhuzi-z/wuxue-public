package com.wuda.wuxue.ui.toolkit;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.network.LibSeatNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.List;

import okhttp3.FormBody;

public class OrderSeatViewModel extends BaseResponseViewModel<String> {
    private MutableLiveData<List<OptionPair>> startTimeList;
    private MutableLiveData<List<OptionPair>> endTimeList;

    OptionPair selectedStartTime;
    OptionPair selectedEndTime;

//    List<OptionPair> tokens;

    public MutableLiveData<List<OptionPair>> getStartTimeList() {
        if (startTimeList == null)
            startTimeList = new MutableLiveData<>();
        return startTimeList;
    }

    public MutableLiveData<List<OptionPair>> getEndTimeList() {
        if (endTimeList == null)
            endTimeList = new MutableLiveData<>();
        return endTimeList;
    }

    public void requestStartTimeList(String seat, String date) {
        LibSeatNetwork.requestStartTime(seat, date, new ResponseHandler<List<OptionPair>>() {
            @Override
            public void onHandle(ResponseResult<List<OptionPair>> result) {
                if (result.isSuccess()) {
                    getStartTimeList().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void requestEndTimeList(String seat, String date) {
        LibSeatNetwork.requestEndTime(seat, date, selectedStartTime.getValue(), new ResponseHandler<List<OptionPair>>() {
            @Override
            public void onHandle(ResponseResult<List<OptionPair>> result) {
                if (result.isSuccess()) {
                    getEndTimeList().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void orderSeat(String seat, String date, List<OptionPair> tokens) {

        FormBody.Builder formBuilder = new FormBody.Builder();

        for (OptionPair pair: tokens) {
            formBuilder.add(pair.getName(), pair.getValue());
        }
        formBuilder.add("date", date);
        formBuilder.add("seat", seat);
        formBuilder.add("start", selectedStartTime.getValue());
        formBuilder.add("end", selectedEndTime.getValue());
        formBuilder.add("authid", "-1");

        LibSeatNetwork.orderSeat(formBuilder.build(), new ResponseHandler<String>() {
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
