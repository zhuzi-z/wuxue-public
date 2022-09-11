package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;

import androidx.lifecycle.ViewModel;

import com.wuda.wuxue.bean.OptionPair;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class LibSeatSharedViewModel extends ViewModel {
    String date;
    Boolean today;
    // "SYNCHRONIZER_TOKEN" "SYNCHRONIZER_URI"
    // 提交预约申请时需要提交
    List<OptionPair> tokens;

    public LibSeatSharedViewModel() {
        today = false; // change => true
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE) >= 22 * 60 + 45) {
            today = true;
        }
        // 初始化与实际相反，复用ChangeDate
        changeDate();
    }

    void changeDate() {
        today = !today;
        Calendar calendar = Calendar.getInstance();
        if (!today) {
            calendar.add(Calendar.DATE, 1);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(calendar.getTime());
    }
}
