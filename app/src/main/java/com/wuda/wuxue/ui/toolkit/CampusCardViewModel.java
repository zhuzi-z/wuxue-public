package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import com.wuda.wuxue.bean.CampusCardBill;
import com.wuda.wuxue.bean.CampusCardInfo;
import com.wuda.wuxue.network.CampusCardNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CampusCardViewModel extends BaseResponseViewModel<String> {
    private MutableLiveData<String> loginResponse;
    private MutableLiveData<CampusCardInfo> cardInfoResponse;
    private MutableLiveData<List<CampusCardBill>> billListResponse;
    private MutableLiveData<String> depositResponse;
    private MutableLiveData<String> lostResponse;

    CampusCardInfo cardInfo;
    String billStartDate;
    String billEndDate;

    public CampusCardViewModel() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        billStartDate = df.format(Calendar.getInstance().getTime());
        billEndDate = billStartDate;
    }

    public MutableLiveData<String> getLoginResponse() {
        if (loginResponse == null) {
            loginResponse = new MutableLiveData<>();
        }
        return loginResponse;
    }

    public MutableLiveData<CampusCardInfo> getCardInfoResponse() {
        if (cardInfoResponse == null) {
            cardInfoResponse = new MutableLiveData<>();
        }
        return cardInfoResponse;
    }

    public MutableLiveData<List<CampusCardBill>> getBillListResponse() {
        if (billListResponse == null) {
            billListResponse = new MutableLiveData<>();
        }
        return billListResponse;
    }

    public MutableLiveData<String> getDepositResponse() {
        if (depositResponse == null) {
            depositResponse = new MutableLiveData<>();
        }
        return depositResponse;
    }

    public MutableLiveData<String> getLostResponse() {
        if (lostResponse == null)
            lostResponse = new MutableLiveData<>();
        return lostResponse;
    }

    public void login() {
        CampusCardNetwork.login(new ResponseHandler<String>() {
            @Override
            public void onHandle(ResponseResult<String> result) {
                if (result.isSuccess())
                    getLoginResponse().postValue(result.getData());
                else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void requestCardInfo() {
        CampusCardNetwork.requestCardInfo(new ResponseHandler<CampusCardInfo>() {
            @Override
            public void onHandle(ResponseResult<CampusCardInfo> result) {
                if (result.isSuccess()) {
                    getCardInfoResponse().postValue(result.getData());
                    cardInfo = result.getData();
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void requestBillList() {
        CampusCardNetwork.requestBillList(cardInfo.getAccount(), billStartDate, billEndDate, new ResponseHandler<List<CampusCardBill>>() {
            @Override
            public void onHandle(ResponseResult<List<CampusCardBill>> result) {
                if (result.isSuccess()) {
                    getBillListResponse().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void deposit(Double amount, String password) {
        CampusCardNetwork.deposit(amount, cardInfo.getAccount(), password, new ResponseHandler<String>() {
            @Override
            public void onHandle(ResponseResult<String> result) {
                if (result.isSuccess())
                    getDepositResponse().postValue(result.getData());
                else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void lost(String password) {
        CampusCardNetwork.lost(cardInfo.getAccount(), password, !cardInfo.isLost(), new ResponseHandler<String>() {
            @Override
            public void onHandle(ResponseResult<String> result) {
                if (result.isSuccess())
                    getLostResponse().postValue(result.getData());
                else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }
}
