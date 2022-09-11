package com.wuda.wuxue.bean;

import com.wuda.wuxue.ui.toolkit.SeatOptionFragment;
import com.wuda.wuxue.network.ServerURL;

public class SeatSelectorWithOptions implements Tool {

    @Override
    public String getToolName() {
        return "自选座位";
    }

    @Override
    public String getUrl() {
        return ServerURL.LIB_SEAT_LOGIN;
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return SeatOptionFragment.class;
    }
}
