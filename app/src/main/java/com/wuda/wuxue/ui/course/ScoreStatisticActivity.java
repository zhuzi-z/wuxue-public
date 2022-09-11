package com.wuda.wuxue.ui.course;

import android.os.Bundle;

import com.wuda.wuxue.ui.base.NavHostActivity;


public class ScoreStatisticActivity extends NavHostActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationTo(new ScoreStatisticFragment(), false);
    }
}