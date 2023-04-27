package com.wuda.wuxue.ui.toolkit;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.SeatLocalHistory;
import com.wuda.wuxue.bean.SeatOnlineHistory;
import com.wuda.wuxue.db.LibSeatDBUtility;
import com.wuda.wuxue.network.LibSeatNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.ArrayList;
import java.util.List;

public class LibSeatViewModel extends BaseResponseViewModel<List<OptionPair>> {

    private MutableLiveData<List<SeatOnlineHistory>> onlineHistory;
    private MutableLiveData<List<SeatLocalHistory>> localHistory;
    private MutableLiveData<String> cancelResponse;

    int onlineHistoryOffset = 0;

    public MutableLiveData<List<SeatOnlineHistory>> getOnlineHistory() {
        if (onlineHistory == null)
            onlineHistory = new MutableLiveData<>();
        return onlineHistory;
    }

    public MutableLiveData<List<SeatLocalHistory>> getLocalHistory() {
        if (localHistory == null)
            localHistory = new MutableLiveData<>();
        return localHistory;
    }

    public MutableLiveData<String> getCancelResponse() {
        if (cancelResponse == null)
            cancelResponse = new MutableLiveData<>();
        return cancelResponse;
    }

    public void login() {
        LibSeatNetwork.login(new ResponseHandler<List<OptionPair>>() {
            @Override
            public void onHandle(ResponseResult<List<OptionPair>> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void requestOnlineHistory() {
        LibSeatNetwork.requestOnlineHistory(onlineHistoryOffset, new ResponseHandler<List<SeatOnlineHistory>>() {
            @Override
            public void onHandle(ResponseResult<List<SeatOnlineHistory>> result) {
                if (result.isSuccess()) {
                    getOnlineHistory().postValue(result.getData());
                    onlineHistoryOffset = result.getTotalPages();
                } else
                    getFailResponse().postValue(result);
            }
        });
    }

    public void queryLocalHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<SeatLocalHistory> seatLocalHistoryList = LibSeatDBUtility.queryLocalHistory();
                localHistory.postValue(seatLocalHistoryList);
            }
        }).start();
    }

    public void cancelOrder(String visaId) {
        LibSeatNetwork.cancelOrder(visaId, new ResponseHandler<String>() {
            @Override
            public void onHandle(ResponseResult<String> result) {
                if (result.isSuccess())
                    getCancelResponse().postValue(result.getData());
                else
                    getFailResponse().postValue(result);
            }
        });
    }

    public boolean hasMoreOnlineHistory() {
        return onlineHistoryOffset != -1;
    }
}
