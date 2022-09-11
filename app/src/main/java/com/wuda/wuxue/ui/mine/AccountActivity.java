package com.wuda.wuxue.ui.mine;

import android.content.Intent;
import android.os.Bundle;

import com.wuda.wuxue.ui.base.NavHostActivity;

public class AccountActivity extends NavHostActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        boolean login = intent.getBooleanExtra("login", true);
        if (login) {
            getSupportActionBar().setTitle("登录");
            navigationTo(new AccountLoginFragment(), false);
        } else {
            getSupportActionBar().setTitle("帐号管理");
            navigationTo(new AccountInfoFragment(), false);
        }
    }
}