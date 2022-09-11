package com.wuda.wuxue.ui.mine;

import android.os.Bundle;

import com.wuda.wuxue.ui.base.NavHostActivity;

public class InfoActivity extends NavHostActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("消息订阅");
        navigationTo(new SubscribeInfoFragment(), false);
    }
}