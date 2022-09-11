package com.wuda.wuxue.ui.toolkit;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.Room;
import com.wuda.wuxue.bean.Seat;
import com.wuda.wuxue.db.LibSeatDBUtility;
import com.wuda.wuxue.network.LibSeatNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatViewModel extends BaseResponseViewModel<List<Seat>> {
    Integer scrollX;
    Integer scrollY;

    private MutableLiveData<Map<String, List<OptionPair>>> allOptions;
    private MutableLiveData<List<OptionPair>> rooms;

    List<OptionPair> roomList;

    Map<String, OptionPair> selectedOptions = new HashMap<>();

    public MutableLiveData<Map<String, List<OptionPair>>> getAllOptions() {
        if (allOptions == null)
            allOptions = new MutableLiveData<>();
        return allOptions;
    }

    public MutableLiveData<List<OptionPair>> getRooms() {
        if (rooms == null)
            rooms = new MutableLiveData<>();
        return rooms;
    }

    public void requestOptions() {
        LibSeatNetwork.requestOptions(new ResponseHandler<Map<String, List<OptionPair>>>() {
            @Override
            public void onHandle(ResponseResult<Map<String, List<OptionPair>>> result) {
                if (result.isSuccess()) {
                    Map<String, List<OptionPair>> options = result.getData();
                    LibSeatDBUtility.saveAllOptionPairs(options);
                    getAllOptions().postValue(options);
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void queryOptions() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, List<OptionPair>> options = LibSeatDBUtility.queryAllOptionPairs();
                if (options.get(OptionPair.SEAT_BUILDING).isEmpty()) {
                    requestOptions();
                } else {
                    getAllOptions().postValue(options);
                }
            }
        }).start();
    }

    public void requestRooms(String building) {
        LibSeatNetwork.requestRooms(building, new ResponseHandler<List<Room>>() {
            @Override
            public void onHandle(ResponseResult<List<Room>> result) {
                if (result.isSuccess()) {
                    LibSeatDBUtility.saveRooms(result.getData());
                    queryRooms(building);
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void queryRooms(String building) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                roomList = LibSeatDBUtility.queryRooms(building);
                if (roomList.isEmpty()) {
                    requestRooms(building);
                } else {
                    getRooms().postValue(roomList);
                }
            }
        }).start();
    }

    public void requestSeats(String date) {
        OptionPair room = selectedOptions.get(OptionPair.SEAT_ROOM);
        if (room == null) return;

        LibSeatNetwork.requestSeatsByRoom(room.getValue(), date, new ResponseHandler<List<Seat>>() {
            @Override
            public void onHandle(ResponseResult<List<Seat>> result) {
                if (result.isSuccess())
                    getSuccessResponse().postValue(result.getData());
                else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public OptionPair getOptionPairByValue(String value, List<OptionPair> optionPairList) {
        if (optionPairList == null) return null;
        for (OptionPair optionPair: optionPairList) {
            if (optionPair.getValue().equals(value)) {
                return optionPair;
            }
        }
        return null;
    }
}
