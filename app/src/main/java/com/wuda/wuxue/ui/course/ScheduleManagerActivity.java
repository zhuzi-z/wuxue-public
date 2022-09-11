package com.wuda.wuxue.ui.course;

import android.os.Bundle;

import com.wuda.wuxue.ui.base.NavHostActivity;

public class ScheduleManagerActivity extends NavHostActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationTo(new TableListFragment(), false);
    }
}