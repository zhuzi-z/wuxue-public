package com.wuda.wuxue.ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wuda.wuxue.R;

public class NavHostActivity extends BaseActivity implements NavigationHost {

    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_host);

        toolbar = findViewById(R.id.back_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void navigationTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out);
        transaction.replace(R.id.nav_host_fragment_container, fragment);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}