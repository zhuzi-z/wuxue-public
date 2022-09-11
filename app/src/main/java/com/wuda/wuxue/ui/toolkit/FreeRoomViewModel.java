package com.wuda.wuxue.ui.toolkit;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.CampusBuilding;
import com.wuda.wuxue.network.FreeRoomNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.List;

public class FreeRoomViewModel extends BaseResponseViewModel<List<List<String>>> {
    MutableLiveData<List<CampusBuilding>> campusBuilding;
    String campus;
    String building;
    String date;

    public MutableLiveData<List<CampusBuilding>> getCampusBuilding() {
        if (campusBuilding == null)
            campusBuilding = new MutableLiveData<>();
        return campusBuilding;
    }

    public void queryCampusBuilding() {
        FreeRoomNetwork.requestBuildings(new ResponseHandler<List<CampusBuilding>>() {
            @Override
            public void onHandle(ResponseResult<List<CampusBuilding>> result) {
                if (result.isSuccess())
                    getCampusBuilding().postValue(result.getData());
                else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void requestFreeRooms() {
        FreeRoomNetwork.requestRooms(building, date, new ResponseHandler<List<List<String>>>() {
            @Override
            public void onHandle(ResponseResult<List<List<String>>> result) {
                if (result.isSuccess())
                    getSuccessResponse().postValue(result.getData());
                else
                    getFailResponse().postValue(result);
            }
        });
    }
}
