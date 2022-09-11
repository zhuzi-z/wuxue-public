package com.wuda.wuxue.ui.mine;

import android.os.Bundle;

import com.wuda.wuxue.ui.base.NavHostActivity;

public class AboutActivity extends NavHostActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle("关于");
        navigationTo(new AboutFragment(), false);
    }
}