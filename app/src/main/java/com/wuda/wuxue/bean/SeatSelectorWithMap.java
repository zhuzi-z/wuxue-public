package com.wuda.wuxue.bean;

import com.wuda.wuxue.ui.toolkit.SeatMapFragment;
import com.wuda.wuxue.network.ServerURL;

public class SeatSelectorWithMap implements Tool {

    @Override
    public String getToolName() {
        return "布局选座";
    }

    @Override
    public String getUrl() {
        return ServerURL.LIB_SEAT_LOGIN;
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return SeatMapFragment.class;
    }
}
