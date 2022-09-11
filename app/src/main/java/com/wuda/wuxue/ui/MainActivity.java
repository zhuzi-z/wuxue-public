package com.wuda.wuxue.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
//import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wuda.wuxue.R;
import com.wuda.wuxue.ui.base.BaseActivity;
import com.wuda.wuxue.ui.base.FullScreenDialog;
import com.wuda.wuxue.ui.mine.StatementFragment;
import com.wuda.wuxue.util.SharePreferenceManager;


public class MainActivity extends BaseActivity {

    BottomNavigationView navView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

        if (SharePreferenceManager.loadBoolean(SharePreferenceManager.APP_FIRST_START, true)) {
            final FullScreenDialog[] fullScreenDialog = {new FullScreenDialog()};
            fullScreenDialog[0].setOnCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            final StatementFragment[] statementFragment = {new StatementFragment()};
            statementFragment[0].setShowCheck(true);
            statementFragment[0].setOnCheckClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fullScreenDialog[0].dismiss();
                    SharePreferenceManager.storeBoolean(SharePreferenceManager.APP_FIRST_START, false);
                }
            });
            fullScreenDialog[0].setFragment(statementFragment[0]);
            fullScreenDialog[0].show(getSupportFragmentManager());
        }

        // 启动页：默认（toolkit）
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        if (SharePreferenceManager.loadString("startup").equals("schedule")) {
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.bottom_navigation);
            navGraph.setStartDestination(R.id.navigation_schedule);
            navController.setGraph(navGraph);
        }

        NavigationUI.setupWithNavController(navView, navController);
    }
}